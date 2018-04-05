package com.composum.pages.commons.widget;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * a 'checkbox' model to select the first value of an options list if checked,
 * if not checked the value is the second value of the options list
 */
public class CheckSelect extends OptionsWidget {

    /**
     * @return 'true' if the current value equals to the first option
     */
    public boolean isChecked() {
        List<Option> options = getOptions();
        return options.size() > 0 && options.get(0).isSelected();
    }

    /**
     * @return 'true' no second option available (no property if not checked)
     */
    public boolean isRemovable() {
        return getOptions().size() < 2;
    }

    /**
     * @return the first - the checked - value for the input field
     */
    public String getInputValue() {
        List<Option> options = getOptions();
        return options.size() > 0 ? options.get(0).getValue() : "";
    }

    /**
     * @return the second - the not checked - value for the hidden field (empty if no 2nd option)
     */
    public String getSecondValue() {
        List<Option> options = getOptions();
        return options.size() > 1 ? options.get(1).getValue() : "";
    }

    /**
     * @return 'true' if a second option is available and not empty
     */
    public boolean getHasSecondValue() {
        return StringUtils.isNotBlank(getSecondValue());
    }

    /**
     * @return 'checked' string if the checkbox has to be checked initially
     */
    public String getCheckedValue() {
        return isChecked() ? Checkbox.CHECKED_TAG_ATTR : "";
    }
}
