package M10Robot.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
public class BoundFieldPatch {
    public static SpireField<Boolean> boundField = new SpireField<>(() -> Boolean.FALSE);
}
