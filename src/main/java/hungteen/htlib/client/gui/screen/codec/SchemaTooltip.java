package hungteen.htlib.client.gui.screen.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.client.resources.language.I18n;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * schema 的约束/默认值公共逻辑：约束格式化（悬浮提示用）与约束校验（编辑器报错用）。
 * 查看器与编辑器共用，约束串格式与 Codec 侧序列化保持一致。
 */
public final class SchemaTooltip {

    private SchemaTooltip() {
    }

    /** 校验的取值语义。 */
    public enum Kind {
        /** 数值：{@code Range / Min / Max}。 */
        NUMBER,
        /** 字符串：{@code Length[min, max]}，按字符数。 */
        STRING,
        /** 容器：{@code Size[min, max]}，按元素个数。 */
        SIZE
    }

    private static final Pattern RANGE = Pattern.compile("^Range\\[min=(.*), max=(.*)]$");
    private static final Pattern MIN = Pattern.compile("^Min\\[value=(.*)]$");
    private static final Pattern MAX = Pattern.compile("^Max\\[value=(.*)]$");
    private static final Pattern LENGTH = Pattern.compile("^Length\\[min=(\\d+), max=(\\d+)]$");
    private static final Pattern SIZE_PATTERN = Pattern.compile("^Size\\[min=(\\d+), max=(\\d+)]$");
    private static final Pattern PATTERN = Pattern.compile("^Pattern\\[regex=(.*)]$");

    /** 单条约束串 → 可读文本（未知格式原样返回）。 */
    public static String formatConstraint(String s) {
        if (s == null) {
            return "";
        }
        Matcher m;
        if ((m = RANGE.matcher(s)).matches()) {
            return "∈ [" + formatNum(m.group(1)) + ", " + formatNum(m.group(2)) + "]";
        }
        if ((m = MIN.matcher(s)).matches()) {
            return "≥ " + formatNum(m.group(1));
        }
        if ((m = MAX.matcher(s)).matches()) {
            return "≤ " + formatNum(m.group(1));
        }
        if ((m = LENGTH.matcher(s)).matches()) {
            return I18n.get("htlib.tooltip.length", m.group(1), m.group(2));
        }
        if ((m = SIZE_PATTERN.matcher(s)).matches()) {
            return I18n.get("htlib.tooltip.size_count", m.group(1), m.group(2));
        }
        if ((m = PATTERN.matcher(s)).matches()) {
            String inner = m.group(1);
            if (inner.length() > 24) {
                inner = inner.substring(0, 24) + "…";
            }
            return I18n.get("htlib.tooltip.pattern", inner);
        }
        return s;
    }

    /** 约束数组 → 分号连接的可读文本；无约束返回空串。 */
    public static String formatConstraints(JsonObject schema) {
        if (schema == null || !schema.has(SchemaKeys.CONSTRAINTS)) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (JsonElement c : schema.getAsJsonArray(SchemaKeys.CONSTRAINTS)) {
            String f = formatConstraint(c.getAsString());
            if (f != null && !f.isEmpty()) {
                parts.add(f);
            }
        }
        return String.join("; ", parts);
    }

    /**
     * 校验单条约束是否满足；返回错误描述（可直接拼在报错里），通过则返回 {@code null}。
     *
     * @param constraint 约束串
     * @param kind       取值语义
     * @param raw        NUMBER 时为数值文本；STRING 时为字符串
     * @param count      SIZE 时为元素个数
     */
    public static String checkConstraint(String constraint, Kind kind, String raw, int count) {
        if (constraint == null) {
            return null;
        }
        Matcher m;
        if (kind == Kind.NUMBER) {
            double v = parseDouble(raw);
            if ((m = RANGE.matcher(constraint)).matches()) {
                double lo = parseDouble(m.group(1)), hi = parseDouble(m.group(2));
                if (v < lo || v > hi) {
                    return "∈ [" + formatNum(m.group(1)) + ", " + formatNum(m.group(2)) + "]";
                }
            } else if ((m = MIN.matcher(constraint)).matches()) {
                double lo = parseDouble(m.group(1));
                if (v < lo) {
                    return "≥ " + formatNum(m.group(1));
                }
            } else if ((m = MAX.matcher(constraint)).matches()) {
                double hi = parseDouble(m.group(1));
                if (v > hi) {
                    return "≤ " + formatNum(m.group(1));
                }
            }
        } else if (kind == Kind.STRING && (m = LENGTH.matcher(constraint)).matches()) {
            int len = raw.length();
            int lo = Integer.parseInt(m.group(1)), hi = Integer.parseInt(m.group(2));
            if (len < lo || len > hi) {
                return I18n.get("htlib.tooltip.length", lo, hi);
            }
        } else if (kind == Kind.SIZE && (m = SIZE_PATTERN.matcher(constraint)).matches()) {
            int lo = Integer.parseInt(m.group(1)), hi = Integer.parseInt(m.group(2));
            if (count < lo || count > hi) {
                return I18n.get("htlib.tooltip.size_count", lo, hi);
            }
        }
        return null;
    }

    /** 数值精简：保留两位小数并去掉多余的 0（如 10.0→10、0.33333333→0.33）。 */
    public static String formatNum(String s) {
        try {
            return new BigDecimal(Double.toString(Double.parseDouble(s.trim())))
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
        } catch (NumberFormatException e) {
            return s.trim();
        }
    }

    /** 取 schema 的默认值文本。 */
    public static String defaultText(JsonObject schema) {
        if (schema == null || !schema.has(SchemaKeys.DEFAULT)) {
            return "";
        }
        JsonElement d = schema.get(SchemaKeys.DEFAULT);
        if (d == null || d.isJsonNull()) {
            return "";
        }
        if (d.isJsonPrimitive() && d.getAsJsonPrimitive().isString()) {
            return d.getAsString();
        }
        return d.toString();
    }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}