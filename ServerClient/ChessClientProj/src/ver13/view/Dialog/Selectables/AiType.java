package ver13.view.Dialog.Selectables;

import ver13.SharedClasses.GameSetup.AiParameters;

import javax.swing.*;
import java.util.ArrayList;

public class AiType implements Selectable {

    public final AiParameters.AiType aiType;

    public AiType(AiParameters.AiType aiType) {
        this.aiType = aiType;
    }

    public static ArrayList<Selectable> selectableAiTypes() {
        ArrayList<Selectable> ret = new ArrayList<>();
        for (AiParameters.AiType aiType : AiParameters.AiType.values()) {
            ret.add(new AiType(aiType));
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
