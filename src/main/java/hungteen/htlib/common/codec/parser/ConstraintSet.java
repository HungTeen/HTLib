package hungteen.htlib.common.codec.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个 Schema 上的一组约束（无序集合，保持添加顺序）。
 *
 * <p>可同时存在多条不同维度的约束（如既有 Range 又有 Size），
 * 解析代码通过 {@link #add} 逐条追加；对外用 {@link #constraints()} 暴露不可变视图。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:20
 **/
public final class ConstraintSet {

    /** 约束列表（按添加顺序）。 */
    private final List<SchemaConstraint> constraints = new ArrayList<>();

    /** 追加一条约束。 */
    public void add(SchemaConstraint constraint) {
        constraints.add(constraint);
    }

    /** 约束的不可变快照。 */
    public List<SchemaConstraint> constraints() {
        return List.copyOf(constraints);
    }

    /** 是否没有任何约束。 */
    public boolean isEmpty() {
        return constraints.isEmpty();
    }

    /**
     * 浅拷贝：复制约束条目（约束本身不可变）。
     * 用于字段级加约束时，避免污染共享 schema（见 {@link DataSchema#copy()}）。
     */
    public ConstraintSet copy() {
        ConstraintSet set = new ConstraintSet();
        set.constraints.addAll(constraints);
        return set;
    }
}