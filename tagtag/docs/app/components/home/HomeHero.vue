<script setup lang="ts">
const { t } = useI18n()
</script>

<template>
  <div class="relative isolate overflow-hidden bg-white dark:bg-neutral-950 min-h-screen flex flex-col justify-center">
    <!-- Star Background -->
    <div class="hidden dark:block">
      <StarsBg
        :star-count="800"
        :speed="'slow'"
        color="#a5b4fc"
      />
    </div>

    <!-- Grain Texture Overlay -->
    <div class="absolute inset-0 z-[1] opacity-[0.04] mix-blend-overlay pointer-events-none bg-[url('https://grainy-gradients.vercel.app/noise.svg')] bg-repeat" />

    <!-- Grid Background Animation -->
    <div class="absolute inset-0 z-0 opacity-20 pointer-events-none">
      <div class="absolute inset-0 bg-[linear-gradient(to_right,#4f4f4f2e_1px,transparent_1px),linear-gradient(to_bottom,#4f4f4f2e_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)]" />
    </div>

    <!-- Glowing Orbs (Dual Tone) -->
    <div class="absolute top-[-10%] left-1/4 w-[800px] h-[800px] bg-cyan-500/20 blur-[120px] rounded-full pointer-events-none mix-blend-screen animate-pulse-slow" />
    <div class="absolute top-[-10%] right-1/4 w-[800px] h-[800px] bg-indigo-500/20 blur-[120px] rounded-full pointer-events-none mix-blend-screen animate-pulse-slow delay-1000" />

    <div class="relative z-10 mx-auto max-w-7xl px-6 pb-24 pt-20 sm:pb-32 lg:flex lg:px-8 lg:py-40">
      <div class="mx-auto max-w-5xl text-center">
        <!-- Logo -->
        <div class="mb-8 flex justify-center">
          <img src="/logo.png" alt="Tagtag Logo" class="h-24 w-auto drop-shadow-2xl animate-fade-in-up" />
        </div>

        <!-- Badge -->
        <div class="mb-8 flex justify-center">
          <div class="relative rounded-full px-3 py-1 text-sm leading-6 text-gray-500 dark:text-neutral-400 ring-1 ring-gray-200 dark:ring-white/10 hover:ring-cyan-500/50 dark:hover:ring-cyan-400/50 hover:bg-gray-50 dark:hover:bg-white/5 transition duration-300 cursor-default backdrop-blur-xl group shadow-[0_0_15px_rgba(6,182,212,0.1)] hover:shadow-[0_0_25px_rgba(6,182,212,0.2)]">
            <span class="bg-gradient-to-r from-cyan-500 to-indigo-500 dark:from-cyan-400 dark:to-indigo-400 bg-clip-text text-transparent font-semibold mr-2 group-hover:brightness-125 transition-all">New</span>
            {{ t('hero.badge') }}
          </div>
        </div>

        <!-- Hero Title -->
        <h1 class="text-5xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-7xl mb-8 leading-tight">
          <span class="inline-block bg-gradient-to-b from-gray-900 via-gray-900 to-gray-600 dark:from-white dark:via-white dark:to-white/40 bg-clip-text text-transparent">
            {{ t('hero.title_part1') }}
          </span>
          <span class="inline-block bg-gradient-to-r from-cyan-400 via-indigo-400 to-purple-400 bg-clip-text text-transparent animate-gradient-x bg-[length:200%_auto] drop-shadow-[0_0_30px_rgba(129,140,248,0.3)]">
            {{ t('hero.title_part2') }}
          </span>
        </h1>

        <p class="mt-6 text-lg leading-8 text-gray-600 dark:text-neutral-400 max-w-2xl mx-auto">
          {{ t('hero.description') }}
        </p>

        <div class="mt-12 flex items-center justify-center gap-x-6">
          <UButton
            to="/getting-started/introduction"
            size="xl"
            color="primary"
            variant="solid"
            class="relative px-8 py-4 rounded-full font-bold shadow-[0_0_20px_rgba(6,182,212,0.4)] hover:shadow-[0_0_40px_rgba(6,182,212,0.6)] transition-all duration-300 hover:scale-105"
          >
            <span class="relative z-10">{{ t('hero.start_building') }}</span>
          </UButton>

          <UButton
            to="https://github.com/admin/openwj/tagtag"
            target="_blank"
            size="xl"
            variant="ghost"
            class="px-8 py-4 rounded-full text-gray-700 dark:text-white hover:bg-gray-100 dark:hover:bg-white/10 border border-gray-200 dark:border-white/5 hover:border-gray-300 dark:hover:border-white/20 transition-all duration-300"
            :label="t('hero.view_on_github')"
            icon="i-simple-icons-github"
          />
        </div>

        <!-- 3D Layered Preview -->
        <div class="mt-24 relative group perspective-1000 w-full max-w-4xl mx-auto">
          <!-- Main Container with Tilt -->
          <div class="relative transform-gpu transition-all duration-700 rotate-x-6 group-hover:rotate-x-0 group-hover:scale-[1.02] preserve-3d">
            <!-- LAYER 1: Main IDE Window -->
            <div class="relative z-10 bg-[#0d1117]/90 rounded-xl border border-white/10 shadow-2xl backdrop-blur-xl overflow-hidden ring-1 ring-white/5 transform-gpu transition-all duration-700 group-hover:translate-z-0">
              <!-- Sheen Effect -->
              <div class="absolute inset-0 bg-gradient-to-tr from-white/10 via-white/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-1000 pointer-events-none z-50 mix-blend-overlay" />

              <!-- Window Controls -->
              <div class="h-10 bg-white/5 border-b border-white/5 flex items-center px-4 space-x-2">
                <div class="flex gap-1.5">
                  <div class="w-3 h-3 rounded-full bg-[#ff5f56]" />
                  <div class="w-3 h-3 rounded-full bg-[#ffbd2e]" />
                  <div class="w-3 h-3 rounded-full bg-[#27c93f]" />
                </div>
                <div class="ml-4 text-xs text-neutral-500 font-mono flex-1 text-center">
                  UserController.java
                </div>
                <div class="w-14" /> <!-- Spacer for centering -->
              </div>

              <!-- Code Content -->
              <div class="p-6 text-left overflow-hidden">
                <pre class="text-xs sm:text-sm leading-6 text-gray-300 font-mono"><code><span class="text-[#c678dd]">@RestController</span>
<span class="text-[#c678dd]">@RequestMapping</span>(<span class="text-[#98c379]">"/users"</span>)
<span class="text-[#56b6c2]">public class</span> <span class="text-[#e5c07b]">UserController</span> {

    <!-- <span class="text-[#c678dd]">@Resource</span> -->
    <span class="text-[#56b6c2]">private final</span> <span class="text-[#e5c07b]">UserService</span> userService;

    <span class="text-gray-500">// Integrated Operation Logging & RBAC</span>
    <span class="text-[#c678dd]">@OperationLog</span>(module = <span class="text-[#98c379]">"IAM"</span>, name = <span class="text-[#98c379]">"Create User"</span>)
    <span class="text-[#c678dd]">@RequirePerm</span>(<span class="text-[#98c379]">Permissions.USER_CREATE</span>)
    <span class="text-[#c678dd]">@PostMapping</span>
    <span class="text-[#56b6c2]">public</span> <span class="text-[#e5c07b]">Result</span>&lt;<span class="text-[#e5c07b]">Long</span>&gt; <span class="text-[#61afef]">create</span>(<span class="text-[#c678dd]">@RequestBody</span> <span class="text-[#c678dd]">@Validated</span> <span class="text-[#e5c07b]">UserDTO</span> dto) {
        <span class="text-[#c678dd]">return</span> <span class="text-[#e5c07b]">Result</span>.success(userService.create(dto));
    }
}</code></pre>
              </div>

              <!-- Bottom Glow Reflection -->
              <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-full h-32 bg-indigo-500/10 blur-[60px] pointer-events-none" />
            </div>

            <!-- LAYER 2: Terminal Window (Floating) -->
            <div class="absolute -bottom-12 -right-4 sm:-right-12 z-20 w-[90%] sm:w-[500px] bg-black/90 rounded-lg border border-white/10 shadow-2xl backdrop-blur-md overflow-hidden transform-gpu transition-all duration-700 translate-y-4 translate-z-10 group-hover:translate-y-8 group-hover:translate-x-4 group-hover:rotate-y-2">
              <div class="h-8 bg-white/5 flex items-center px-3 border-b border-white/5">
                <span class="text-[10px] text-gray-500 font-mono">TERMINAL</span>
              </div>
              <div class="p-4 text-left font-mono text-[10px] sm:text-xs leading-5 text-gray-300">
                <div class="flex items-center gap-2">
                  <span class="text-green-400">➜</span> <span>mvn spring-boot:run</span>
                </div>
                <div class="mt-2 text-gray-400">
                  ...
                </div>
                <div class="text-blue-400">
                  [INFO] Starting TagtagApplication v1.0.0...
                </div>
                <div>[INFO] Tomcat initialized with port(s): 8080 (http)</div>
                <div>[INFO] Connected to Redis [localhost:6379]</div>
                <div class="text-green-400 mt-1">
                  [INFO] Started TagtagApplication in 2.14s (JVM running for 2.8s)
                </div>
                <div class="flex items-center gap-2 mt-2 animate-pulse">
                  <span class="text-green-400">➜</span> <span class="w-2 h-4 bg-gray-500 block" />
                </div>
              </div>
            </div>

            <!-- LAYER 3: Status Card (Floating) -->
            <div class="absolute -top-6 -left-4 sm:-left-8 z-30 bg-white dark:bg-[#1c1c1e] p-4 rounded-xl shadow-[0_20px_50px_rgba(0,0,0,0.3)] border border-gray-100 dark:border-white/10 transform-gpu transition-all duration-700 -translate-y-2 translate-z-20 group-hover:-translate-y-6 group-hover:-translate-x-4 group-hover:-rotate-y-2">
              <div class="flex items-center gap-3">
                <div class="relative flex h-3 w-3">
                  <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75" />
                  <span class="relative inline-flex rounded-full h-3 w-3 bg-green-500" />
                </div>
                <div>
                  <div class="text-xs text-gray-500 dark:text-gray-400 font-medium">
                    System Status
                  </div>
                  <div class="text-sm font-bold text-gray-900 dark:text-white">
                    All Systems Operational
                  </div>
                </div>
              </div>
              <div class="mt-3 flex gap-4 text-xs font-mono text-gray-500">
                <div>
                  <div class="text-[10px] uppercase tracking-wider mb-0.5">
                    Latency
                  </div>
                  <div class="text-green-500 font-bold">
                    12ms
                  </div>
                </div>
                <div>
                  <div class="text-[10px] uppercase tracking-wider mb-0.5">
                    Uptime
                  </div>
                  <div class="text-gray-900 dark:text-gray-200 font-bold">
                    99.9%
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.perspective-1000 {
  perspective: 1000px;
}
.preserve-3d {
  transform-style: preserve-3d;
}
.rotate-x-6 {
  transform: rotateX(20deg) rotateY(-10deg);
}
.translate-z-0 {
  transform: translateZ(0px);
}
.translate-z-10 {
  transform: translateZ(30px);
}
.translate-z-20 {
  transform: translateZ(60px);
}
</style>
