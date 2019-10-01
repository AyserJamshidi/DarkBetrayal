package com.ne.gs.model.items;

import com.ne.gs.model.stats.calc.functions.StatFunction;
import com.ne.gs.model.templates.stats.ModifiersTemplate;

public class RandomBonusResult {

    private final ModifiersTemplate template;
    private final int templateNumber;

    public RandomBonusResult(ModifiersTemplate template, int number) {
        this.template = template;
        for (StatFunction function : template.getModifiers())
            function.setRandomNumber(number);
        this.templateNumber = number;
    }

    public ModifiersTemplate getTemplate() {
        return template;
    }

    public int getTemplateNumber() {
        return templateNumber;
    }

}
