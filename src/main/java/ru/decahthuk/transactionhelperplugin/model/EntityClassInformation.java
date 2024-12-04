package ru.decahthuk.transactionhelperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.decahthuk.transactionhelperplugin.utils.EntityPsiClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about entities
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EntityClassInformation {

    @Setter
    private String className;

    private List<String> lazyFieldNames = new ArrayList<>();

    private List<String> lazyFieldGetters = new ArrayList<>();

    public void addLazyField(String fieldName) {
        lazyFieldNames.add(fieldName);
        lazyFieldGetters.add(EntityPsiClassUtils.convertFieldNameToGetter(fieldName));
    }
}
