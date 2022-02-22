package ver6.view.dialogs.game_select.selectable;

import ver6.SharedClasses.GameSetup.AiParameters;
import ver6.view.dialogs.Selectable;

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
