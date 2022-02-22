package ver8.view.Wishfull.dialogs.GameSelect.Selectables;

import ver8.SharedClasses.GameSetup.AiParameters;
import ver8.view.Wishfull.dialogs.Selectable;

import javax.swing.*;
import java.util.ArrayList;

public class SelectableAiType implements Selectable {

    public final AiParameters.AiType aiType;

    public SelectableAiType(AiParameters.AiType aiType) {
        this.aiType = aiType;
    }

    public static ArrayList<Selectable> selectableAiTypes() {
        ArrayList<Selectable> ret = new ArrayList<>();
        for (AiParameters.AiType aiType : AiParameters.AiType.values()) {
            ret.add(new SelectableAiType(aiType));
        }
        return ret;
    }


    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getText() {
        return aiType.name();
    }
}
