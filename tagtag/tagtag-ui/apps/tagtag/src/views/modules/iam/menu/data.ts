import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { z } from '#/adapter/form';

export const columns: VxeGridProps['columns'] = [
  { type: 'checkbox', width: 50 },
  // { title: '序号', type: 'seq', width: 50 },
  { field: 'id', title: 'ID', visible: false },
  { field: 'icon', title: '图标', slots: { default: 'icon' }, width: 80 },
  { field: 'menuName', title: '菜单名称', treeNode: true, minWidth: 150 },
  {
    field: 'menuType',
    title: '菜单类型',
    slots: { default: 'type' },
    width: 100,
  },
  { field: 'menuCode', title: '权限标识', minWidth: 120 },
  { field: 'path', title: '路由地址', minWidth: 120 },
  { field: 'component', title: '组件路径', minWidth: 150 },
  { field: 'sort', title: '排序', width: 80 },
  { field: 'status', title: '状态', slots: { default: 'status' }, width: 100 },
  {
    field: 'isExternal',
    title: '外链',
    slots: { default: 'external' },
    width: 80,
  },
  { field: 'remark', title: '备注', minWidth: 120 },
  {
    field: 'action',
    fixed: 'right',
    slots: { default: 'action' },
    title: '操作',
    width: 120,
  },
];

export const searchFormSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    fieldName: 'menuName',
    label: '菜单名称',
    componentProps: {
      placeholder: '请输入菜单名称',
    },
  },
  {
    component: 'Select',
    fieldName: 'menuType',
    label: '菜单类型',
    componentProps: {
      placeholder: '请选择菜单类型',
      allowClear: true,
      options: [
        {
          label: '目录',
          value: 0,
        },
        {
          label: '菜单',
          value: 1,
        },
        {
          label: '按钮',
          value: 2,
        },
      ],
    },
  },
  {
    component: 'Select',
    fieldName: 'status',
    label: '状态',
    componentProps: {
      placeholder: '请选择状态',
      allowClear: true,
      options: [
        {
          label: '启用',
          value: 1,
        },
        {
          label: '禁用',
          value: 0,
        },
      ],
    },
  },
];

export const formSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    dependencies: {
      show: false,
      // 随意一个字段改变时，都会触发
      triggerFields: ['id'],
    },
    fieldName: 'id',
    label: 'ID',
  },
  {
    component: 'RadioGroup',
    fieldName: 'menuType',
    label: '菜单类型',
    componentProps: {
      optionType: 'button',
      buttonStyle: 'solid',
      options: [
        {
          label: '目录',
          value: 0,
        },
        {
          label: '菜单',
          value: 1,
        },
        {
          label: '按钮',
          value: 2,
        },
      ],
    },
    defaultValue: 0,
    rules: 'selectRequired',
    formItemClass: 'col-span-2',
  },
  {
    component: 'TreeSelect',
    componentProps: {
      placeholder: '请选择',
      class: 'w-full',
      treeData: [],
      treeLine: true,
      allowClear: true,
      treeDefaultExpandAll: true,
      showSearch: true,
      fieldNames: {
        children: 'children',
        label: 'menuName',
        value: 'id',
      },
    },
    fieldName: 'parentId',
    label: '上级菜单',
    formItemClass: 'col-span-2',
  },

  {
    component: 'Input',
    fieldName: 'menuName',
    label: '菜单名称',
    rules: z
      .string()
      .min(1, '请输入菜单名称')
      .max(50, '菜单名称不能超过50个字符'),
    componentProps: {
      placeholder: '请输入菜单名称',
      maxlength: 50,
      showCount: true,
    },
  },
  {
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入',
      class: 'w-full',
      min: 0,
    },
    fieldName: 'sort',
    label: '排序',
    rules: z.number().min(0, '排序值不能小于0'),
  },
  {
    component: 'Input',
    fieldName: 'menuCode',
    label: '权限标识',
    rules: z
      .string()
      .min(1, '请输入权限标识')
      .max(100, '权限标识不能超过100个字符')
      .regex(
        /^[a-z][\w:-]*$/i,
        '权限标识必须以字母开头，只能包含字母、数字、下划线、冒号和连字符',
      ),
    componentProps: {
      placeholder: '请输入权限标识，如：sys:menu:add',
      maxlength: 100,
    },
  },
  {
    component: 'IconPicker',
    fieldName: 'icon',
    label: '菜单图标',
    dependencies: {
      show(values) {
        return values.menuType !== 2;
      },
      // 随意一个字段改变时，都会触发
      triggerFields: ['menuType'],
    },
    componentProps: {
      prefix: 'lucide', // 默认使用lucide图标
      placeholder: '请选择图标或输入图标名称',
      // 支持多种图标前缀
      prefixOptions: [
        { label: 'Lucide', value: 'lucide' },
        { label: 'Ant Design', value: 'ant-design' },
        { label: 'Material Design', value: 'mdi' },
        { label: 'Heroicons', value: 'heroicons' },
        { label: 'Tabler', value: 'tabler' },
      ],
    },
    help: '支持Iconify图标库，如：lucide:home、ant-design:home-outlined 或 Tailwind CSS类名：icon-[lucide--home]',
  },
  {
    component: 'Input',
    fieldName: 'path',
    label: '路由地址',
    componentProps: {
      placeholder: '请输入路由地址，如：/system/menu',
      maxlength: 200,
    },
    help: '访问的路由地址，如：`user`，外链时作为路由 key，如：`/external/link`',
    dependencies: {
      show(values) {
        return values.menuType !== 2;
      },
      rules(values) {
        return values.isExternal
          ? z.string().optional()
          : z
              .string()
              .min(1, '请输入路由地址')
              .max(200, '路由地址不能超过200个字符');
      },
      // 随意一个字段改变时，都会触发
      triggerFields: ['menuType', 'isExternal'],
    },
  },
  {
    component: 'Input',
    fieldName: 'component',
    label: '组件路径',
    componentProps: {
      placeholder: '请输入组件路径，如：/views/system/menu/index',
      maxlength: 200,
    },
    formItemClass: 'col-span-2',
    dependencies: {
      show(values) {
        return values.menuType === 1 && !values.isExternal;
      },
      rules(values) {
        return values.isExternal
          ? z.string().optional()
          : z
              .string()
              .min(1, '请输入组件路径')
              .max(200, '组件路径不能超过200个字符');
      },
      // 随意一个字段改变时，都会触发
      triggerFields: ['menuType', 'isExternal'],
    },
  },
  {
    component: 'Switch',
    componentProps: {
      class: 'w-auto',
      checkedChildren: '显示',
      unCheckedChildren: '隐藏',
    },
    fieldName: 'showInMenu',
    label: '是否显示',
    dependencies: {
      show(values) {
        return values.menuType !== 2;
      },
      // 随意一个字段改变时，都会触发
      triggerFields: ['menuType'],
    },
    defaultValue: true,
  },
  {
    component: 'Switch',
    componentProps: {
      class: 'w-auto',
      checkedChildren: '启用',
      unCheckedChildren: '禁用',
    },
    fieldName: 'keepAlive',
    label: '是否缓存',
    dependencies: {
      show(values) {
        return values.menuType === 1;
      },
      // 随意一个字段改变时，都会触发
      triggerFields: ['menuType'],
    },
    defaultValue: false,
  },
  {
    component: 'Switch',
    componentProps: {
      class: 'w-auto',
      checkedChildren: '是',
      unCheckedChildren: '否',
    },
    fieldName: 'isExternal',
    label: '是否外链',
    dependencies: {
      show(values) {
        return values.menuType !== 2;
      },
      // 随意一个字段改变时，都会触发
      triggerFields: ['menuType'],
    },
    defaultValue: false,
  },
  {
    component: 'Input',
    fieldName: 'link',
    label: '外链地址',
    componentProps: {
      placeholder: '请输入外链地址，如：https://www.example.com',
      maxlength: 500,
    },
    formItemClass: 'col-span-2',
    dependencies: {
      show(values) {
        return values.isExternal;
      },
      rules(values) {
        return values.isExternal
          ? z
              .string()
              .min(1, '请输入外链地址')
              .max(500, '外链地址不能超过500个字符')
              .url('请输入有效的URL地址')
          : z.string().optional();
      },
      // 随意一个字段改变时，都会触发
      triggerFields: ['isExternal'],
    },
  },
  {
    component: 'Textarea',
    fieldName: 'remark',
    label: '备注',
    rules: z.string().max(500, '备注不能超过500个字符').optional(),
    componentProps: {
      placeholder: '请输入备注信息',
      rows: 3,
      maxlength: 500,
      showCount: true,
      class: 'w-full',
    },
    formItemClass: 'col-span-2',
  },
  {
    component: 'Switch',
    componentProps: {
      class: 'w-auto',
      checkedChildren: '启用',
      unCheckedChildren: '禁用',
    },
    fieldName: 'status',
    label: '状态',
    defaultValue: true,
  },
];
