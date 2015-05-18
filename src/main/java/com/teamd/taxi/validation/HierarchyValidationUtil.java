package com.teamd.taxi.validation;

import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Set;

/**
 * Created on 14-May-15.
 *
 * @author Nazar Dub
 */
public abstract class HierarchyValidationUtil {
    private final Set<String>[] hierarchyData;

    @SafeVarargs
    public HierarchyValidationUtil(Set<String>... levels) {
        hierarchyData = new Set[levels.length];
        System.arraycopy(levels, 0, hierarchyData, 0, levels.length);
    }

    protected FieldError higherError(FieldError first, FieldError second) {
        for (Set<String> level : hierarchyData) {
            if (level.contains(first.getCode())) {
                return first;
            }
            if (level.contains(second.getCode())) {
                return second;
            }
        }
        return null;
    }

    protected Set<String>[] getHierarchyData() {
        return hierarchyData;
    }

    public abstract List<FieldError> filterErrors(List<FieldError> errors);
}
