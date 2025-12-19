import { onMounted, reactive, toRefs } from 'vue';

import { getDictDataList } from '#/api/modules/system/dict';

/**
 * 字典 Hook
 * @param dictTypes 字典类型数组
 */
export function useDict(...dictTypes: string[]) {
  const dicts = reactive<Record<string, any[]>>({});

  const initDict = async () => {
    // 并行请求所有字典数据
    const promises = dictTypes.map(async (type) => {
      const data = await getDictDataList(type);
      dicts[type] = data.map((item) => ({
        label: item.itemName,
        value: item.itemCode,
        color: item.cssClass, // 假设 cssClass 存储颜色
        ...item,
      }));
    });

    await Promise.all(promises);
  };

  onMounted(() => {
    initDict();
  });

  return {
    ...toRefs(dicts),
  };
}
