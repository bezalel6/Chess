package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.GameSetup.AiParameters;

import javax.swing.*;
import java.util.ArrayList;

/**
 * a selectable Ai type.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class AiType implements Selectable {

    /**
     * The Ai type.
     */
    public final AiParameters.AiType aiType;

    /**
     * Instantiates a new Ai type.
     *
     * @param aiType the ai type
     */
    public AiType(AiParameters.AiType aiType) {
        this.aiType = aiType;
    }

    /**
     * Selectable ai types array list.
     *
     * @return the array list
     */
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
