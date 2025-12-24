package dev.tagtag.module.auth.service.impl;

import dev.tagtag.kernel.constant.CacheConstants;
import dev.tagtag.contract.auth.dto.ImageCaptchaDTO;
import dev.tagtag.module.auth.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

/**
 * 验证码服务实现
 * 使用 Redis 存储验证码值，TTL 由 CacheConstants 配置；图片使用 java.awt 绘制 PNG
 */
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成一次性图片验证码并写入缓存
     * @return 包含图片 DataURL 与 captchaId 的对象
     */
    @Override
    public ImageCaptchaDTO generateImageCaptcha() {
        String text = randomText(5);
        BufferedImage image = drawImage(text);
        String dataUrl = toDataUrl(image);
        String id = UUID.randomUUID().toString();
        String key = CacheConstants.keyCaptcha(id);
        Duration ttl = CacheConstants.CAPTCHA_TTL;
        // 存大写文本，比较时统一大小写
        stringRedisTemplate.opsForValue().set(key, text.toUpperCase(), ttl);
        ImageCaptchaDTO dto = new ImageCaptchaDTO();
        dto.setSrc(dataUrl);
        dto.setCaptchaId(id);
        return dto;
    }

    /**
     * 校验验证码但不删除（通常用于预校验）
     * @param captchaId 验证码唯一标识
     * @param code 用户输入验证码文本
     * @return 是否校验通过
     */
    @Override
    public boolean validate(String captchaId, String code) {
        if (captchaId == null || code == null) return false;
        String key = CacheConstants.keyCaptcha(captchaId);
        String val = stringRedisTemplate.opsForValue().get(key);
        if (val == null || val.isBlank()) return false;
        return val.equalsIgnoreCase(code.trim());
    }

    /**
     * 校验并消费验证码（成功后删除，防重复使用）
     *
     * @param captchaId 验证码唯一标识
     * @param code      用户输入验证码文本
     */
    @Override
    public void validateAndConsume(String captchaId, String code) {
        boolean ok = validate(captchaId, code);
        if (ok) {
            try {
                stringRedisTemplate.delete(CacheConstants.keyCaptcha(captchaId));
            } catch (RuntimeException ignore) {
            }
        }
    }

    /**
     * 生成验证码文本（排除易混淆字符）
     * @param len 长度
     * @return 文本
     */
    private String randomText(int len) {
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    /**
     * 绘制验证码图片
     * @param text 文本
     * @return 图片
     */
    private BufferedImage drawImage(String text) {
        int w = 120, h = 40;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        // 文字
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getAscent();
        g.drawString(text, (w - tw) / 2, (h + th) / 2 - 4);
        // 干扰线
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(r.nextInt(150), r.nextInt(150), r.nextInt(150)));
            int x1 = r.nextInt(w), y1 = r.nextInt(h);
            int x2 = r.nextInt(w), y2 = r.nextInt(h);
            g.drawLine(x1, y1, x2, y2);
        }
        // 噪点
        for (int i = 0; i < 150; i++) {
            int x = r.nextInt(w), y = r.nextInt(h);
            img.setRGB(x, y, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)).getRGB());
        }
        g.dispose();
        return img;
    }

    /**
     * 转为 PNG 的 DataURL
     * @param image 图片
     * @return DataURL 字符串
     */
    private String toDataUrl(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return "data:image/png;base64," + b64;
        } catch (Exception e) {
            return "data:image/png;base64,";
        }
    }
}