package M10Robot.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

public class EchoFieldPatches {

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            EchoFields.echo.set(result, EchoFields.echo.get(self));
            EchoFields.baseEcho.set(result, EchoFields.baseEcho.get(self));
            EchoFields.isEchoUpgraded.set(result, EchoFields.isEchoUpgraded.get(self));
            return result;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class PlayExtraCopies {
        @SpireInsertPatch(locator = Locator.class)
        public static void withoutInfiniteLoopPls(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            if (EchoFields.echo.get(c) > 0) {
                for (int i = 0; i < EchoFields.echo.get(c) ; i++) {
                    AbstractCard tmp = c.makeSameInstanceOf();
                    AbstractDungeon.player.limbo.addToBottom(tmp);
                    tmp.current_x = c.current_x;
                    tmp.current_y = c.current_y;
                    tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
                    tmp.target_y = (float)Settings.HEIGHT / 2.0F;
                    if (monster != null) {
                        tmp.calculateCardDamage(monster);
                    }
                    tmp.purgeOnUse = true;
                    //Dont loop infinitely, lol
                    EchoFields.echo.set(tmp, 0);
                    AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, monster, c.energyOnUse, true, true), true);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
