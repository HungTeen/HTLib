package hungteen.htlib.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射辅助工具：沿继承树读取字段 / 收集实例字段。
 *
 * <p>由于 DFU 与 Minecraft 的 codec 大量使用包私有、私有嵌套类和 lambda 捕获字段，
 * 需要绕过访问控制读取其内部结构。全部调用都做了 try/catch，任何反射失败都安静返回
 * （null / 空列表），保证解析绝不因单个字段失败而中断。</p>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:23
 **/
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    /**
     * 沿继承树（自下而上）读取指定名字字段的值。
     *
     * @param object 目标对象
     * @param name   字段名
     * @return 字段值；字段不存在、不可访问或取值失败时返回 null
     */
    public static Object getField(Object object, String name) {

        Class<?> clazz = object.getClass();

        while (clazz != null && clazz != Object.class) {

            try {

                Field field = clazz.getDeclaredField(name);

                field.setAccessible(true);

                return field.get(object);

            } catch (NoSuchFieldException ignored) {

                // 当前类没有该字段，去父类找。
                clazz = clazz.getSuperclass();

            } catch (Throwable e) {

                // 字段存在但不可访问 / 取值抛异常（如模块未 opens），放弃。
                return null;
            }
        }

        return null;
    }

    /**
     * 收集类及其所有父类（不含 Object）的非静态实例字段（含私有），保持声明顺序。
     *
     * <p>用于对象图 BFS：把每个字段值当作下一步可达节点。跳过了 static 字段，
     * 避免遍历到类元数据 / 常量池产生噪音。</p>
     *
     * @param clazz 起始类
     * @return 可访问的非静态实例字段列表（不可访问的字段被跳过）
     */
    public static List<Field> fields(Class<?> clazz) {

        List<Field> result = new ArrayList<>();

        while (clazz != null && clazz != Object.class) {

            for (Field field : clazz.getDeclaredFields()) {

                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                try {

                    field.setAccessible(true);

                    result.add(field);

                } catch (Throwable ignored) {
                    /*
                     * 其他模块未 opens（如 java.base），跳过不可访问字段，
                     * 避免 InaccessibleObjectException。
                     */
                }
            }

            clazz = clazz.getSuperclass();
        }

        return result;
    }
}