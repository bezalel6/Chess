package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.GameSetup.AiParameters;

import javax.swing.*;
import java.util.ArrayList;

/**
 * a selectable Ai type.
 *
 * @param aiType The Ai type.
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record SelectableAiType(AiParameters.AiType aiType) implements Selectable {

    /**
     * Instantiates a new Ai type.
     *
     * @param aiType the ai type
     */
    public SelectableAiType {
    }

    /**
     * Selectable ai types array list.
     *
     * @return the array list
     */
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
