package com.zetta.android.device.actions;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.apigee.zettakit.ZIKTransition;
import com.zetta.android.ImageLoader;
import com.zetta.android.ListItem;
import com.zetta.android.ZettaStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionListItemParser {

    @NonNull
    public ListItem parseActionListItem(ZettaStyle style, ZIKTransition transition) {
        List<Map<String, Object>> eventFields = transition.getFields();
        int deviceForegroundColor = style.getForegroundColor();
        int deviceBackgroundColor = style.getBackgroundColor();
        if (eventFields.size() == 1) {
            String action = transition.getName();
            ColorStateList actionInputColorList = ColorStateList.valueOf(deviceForegroundColor);
            ColorStateList actionTextColorList = ColorStateList.valueOf(deviceBackgroundColor);
            Drawable foregroundDrawable = ImageLoader.Drawables.getBackgroundDrawableFor(deviceForegroundColor);
            Drawable backgroundDrawable = ImageLoader.Drawables.getBackgroundDrawableFor(deviceBackgroundColor);
            return new ActionToggleListItem(action, action, actionInputColorList, actionTextColorList, foregroundDrawable, backgroundDrawable);
        } else if (eventFields.size() == 2) {
            Map<String, Object> eventField = eventFields.get(0);
            String label = String.valueOf(eventField.get("name"));
            String action = transition.getName();
            ColorStateList actionInputColorList = ColorStateList.valueOf(deviceForegroundColor);
            ColorStateList actionTextColorList = ColorStateList.valueOf(deviceBackgroundColor);
            Drawable foregroundDrawable = ImageLoader.Drawables.getBackgroundDrawableFor(deviceForegroundColor);
            Drawable backgroundDrawable = ImageLoader.Drawables.getBackgroundDrawableFor(deviceBackgroundColor);
            return new ActionSingleInputListItem(label, action, actionInputColorList, actionTextColorList, foregroundDrawable, backgroundDrawable);
        } else {
            List<String> labels = new ArrayList<>();
            for (Map<String, Object> eventField : eventFields) {
                String label = String.valueOf(eventField.get("name"));
                labels.add(label);
            }
            String action = transition.getName();
            ColorStateList actionInputColorList = ColorStateList.valueOf(deviceForegroundColor);
            ColorStateList actionTextColorList = ColorStateList.valueOf(deviceBackgroundColor);
            Drawable foregroundDrawable = ImageLoader.Drawables.getBackgroundDrawableFor(deviceForegroundColor);
            Drawable backgroundDrawable = ImageLoader.Drawables.getBackgroundDrawableFor(deviceBackgroundColor);
            return new ActionMultipleInputListItem(labels, action, actionInputColorList, actionTextColorList, foregroundDrawable, backgroundDrawable);
        }
    }

}
