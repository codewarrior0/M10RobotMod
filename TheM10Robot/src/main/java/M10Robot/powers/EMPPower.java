package M10Robot.powers;

import M10Robot.M10RobotMod;
import basemod.BaseMod;
import basemod.interfaces.CloneablePowerInterface;
import basemod.interfaces.PostEnergyRechargeSubscriber;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class EMPPower extends AbstractPower implements CloneablePowerInterface, PostEnergyRechargeSubscriber {

    public static final String POWER_ID = M10RobotMod.makeID("EMPPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.
    // There's a fallback "missing texture" image, so the game shouldn't crash if you accidentally put a non-existent file.
    //private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    //private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));

    public EMPPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.priority = 1000;

        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        // We load those txtures here.
        //this.loadRegion("cExplosion");
        this.loadRegion("stasis");
        //this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        //this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }

    }

    @Override
    public void onInitialApplication() {
        BaseMod.subscribe(this);
    }

    @Override
    public void onRemove() {
        BaseMod.unsubscribe(this);
    }

    @Override
    public AbstractPower makeCopy() {
        return new EMPPower(owner, amount);
    }

    @Override
    public void receivePostEnergyRecharge() {
        this.addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
    }
}

