package M10Robot.orbs;

import M10Robot.M10RobotMod;
import M10Robot.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LockOnPower;
import com.megacrit.cardcrawl.vfx.combat.*;

import static M10Robot.M10RobotMod.makeOrbPath;

public class BitOrb extends AbstractCustomOrb {

    // Standard ID/Description
    public static final String ORB_ID = M10RobotMod.makeID("BitOrb");
    private static final OrbStrings orbString = CardCrawlGame.languagePack.getOrbString(ORB_ID);
    public static final String[] DESC = orbString.DESCRIPTION;

    public static final Texture IDLE_IMG = TextureLoader.getTexture(makeOrbPath("BitOrb_00s.png"));
    public static final Texture ATTACK_IMG = TextureLoader.getTexture(makeOrbPath("BitOrb_01s.png"));
    public static final Texture HURT_IMG = TextureLoader.getTexture(makeOrbPath("BitOrb_02s.png"));
    public static final Texture SUCCESS_IMG = TextureLoader.getTexture(makeOrbPath("BitOrb_03s.png"));
    public static final Texture FAILURE_IMG = TextureLoader.getTexture(makeOrbPath("BitOrb_04s.png"));
    public static final Texture THROW_IMG = TextureLoader.getTexture(makeOrbPath("BitOrb_05s.png"));
    // Animation Rendering Numbers - You can leave these at default, or play around with them and see what they change.
    private float vfxTimer = 1.0f;
    private float vfxIntervalMin = 0.1f;
    private float vfxIntervalMax = 0.4f;
    private static final float ORB_WAVY_DIST = 0.04f;
    private static final float PI_4 = 12.566371f;
    private static final float MOUTH_OFFSET_X = -10f * Settings.scale;
    private static final float MOUTH_OFFSET_Y = -35f * Settings.scale;

    public BitOrb() {

        super(IDLE_IMG, ATTACK_IMG, HURT_IMG, SUCCESS_IMG, FAILURE_IMG, THROW_IMG);
        ID = ORB_ID;
        name = orbString.NAME;

        linkedPower = new BitOrbPower(this);

        evokeAmount = baseEvokeAmount = 0;
        passiveAmount = basePassiveAmount = 1;

        updateDescription();

        //angle = MathUtils.random(360.0f); // More Animation-related Numbers
        channelAnimTimer = 0.5f;
    }

    @Override
    public void updateDescription() { // Set the on-hover description of the orb
        applyFocus(); // Apply Focus (Look at the next method)
        description = DESC[0] + passiveAmount + DESC[1] + DESC[2] + evokeAmount + DESC[3]; // Set the description
    }

    public void applyFocus() {
        AbstractPower power = AbstractDungeon.player.getPower("Focus");
        if (power != null) {
            this.passiveAmount = this.basePassiveAmount + power.amount;
        } else {
            this.passiveAmount = this.basePassiveAmount;
        }
    }

    @Override
    public void onEvoke() { // 1.On Orb Evoke
        AbstractCreature m = AbstractDungeon.getRandomMonster();
        int damage = this.evokeAmount;
        if (p.hasPower(LockOnPower.POWER_ID)) {
            damage = (int)(damage * 1.5F);
        }
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                playAnimation(ATTACK_IMG, MED_ANIM);
                this.isDone = true;
            }
        });
        //this.addToBot(new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.5F));
        //this.addToBot(new VFXAction(new SmallLaserEffect(m.hb.cX, m.hb.cY, getXPosition(), getYPosition()), 0.0F));
        this.addToBot(new VFXAction(new ExplosionSmallEffect(m.hb.cX, m.hb.cY), 0.0F));
        this.addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.NONE, true));
        this.addToTop(new RemoveSpecificPowerAction(p, p, linkedPower));
    }

    @Override
    public void onChannel() {
        this.addToBot(new ApplyPowerAction(p, p, linkedPower));
    }

    @Override
    public void onLinkedPowerTrigger() {
        this.addToBot(new VFXAction(new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.FROST), 0.0f));
    }

    @Override
    public void updateAnimation() {// You can totally leave this as is.
        // If you want to create a whole new orb effect - take a look at conspire's Water Orb. It includes a custom sound, too!
        super.updateAnimation();
        //angle += Gdx.graphics.getDeltaTime() * 45.0f;
        vfxTimer -= Gdx.graphics.getDeltaTime();
        if (vfxTimer < 0.0f) {
            AbstractDungeon.effectList.add(new DarkOrbPassiveEffect(cX, cY)); // This is the purple-sparkles in the orb. You can change this to whatever fits your orb.
            vfxTimer = MathUtils.random(vfxIntervalMin, vfxIntervalMax);
        }
    }

    // Render the orb.
    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(new Color(1.0f, 1.0f, 1.0f, c.a));
        sb.draw(img, cX - 48.0f, cY - 48.0f + bobEffect.y, 48.0f, 48.0f, 96.0f, 96.0f, scale + MathUtils.sin(angle / PI_4) * ORB_WAVY_DIST * Settings.scale, scale, angle, 0, 0, 96, 96, false, false);
        renderText(sb);
        hb.render(sb);
    }

    protected void renderText(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.cardEnergyFont_L, Integer.toString(this.evokeAmount), this.cX + NUM_X_OFFSET + GENERIC_X_OFFSET, this.cY + this.bobEffect.y / 2.0F + NUM_Y_OFFSET - 4.0F * Settings.scale, new Color(0.2F, 1.0F, 1.0F, this.c.a), this.fontScale);
        FontHelper.renderFontCentered(sb, FontHelper.cardEnergyFont_L, Integer.toString(this.passiveAmount), this.cX + NUM_X_OFFSET + GENERIC_X_OFFSET, this.cY + this.bobEffect.y / 2.0F + NUM_Y_OFFSET + 20.0F * Settings.scale, this.c, this.fontScale);
    }

    @Override
    public void triggerEvokeAnimation() { // The evoke animation of this orb is the dark-orb activation effect.
        AbstractDungeon.effectsQueue.add(new DarkOrbActivateEffect(cX, cY));
    }

    @Override
    public void playChannelSFX() { // When you channel this orb, the ATTACK_FIRE effect plays ("Fwoom").
        CardCrawlGame.sound.play("ATTACK_FIRE", 0.1f);
    }

    @Override
    public float getXPosition() {
        return super.getXPosition() + MOUTH_OFFSET_X;
    }

    @Override
    public float getYPosition() {
        return super.getYPosition() + MOUTH_OFFSET_Y;
    }

    @Override
    public AbstractOrb makeCopy() {
        return new BitOrb();
    }

    private static class BitOrbPower extends AbstractLinkedOrbPower {

        public BitOrbPower(AbstractCustomOrb linkedOrb) {
            super(linkedOrb);
        }

        @Override
        public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
            if (!DontLoopcast.orbAttack.get(info) && target != owner) {
                int damage = this.linkedOrb.passiveAmount;
                if (this.owner.hasPower(LockOnPower.POWER_ID)) {
                    damage = (int)(damage * 1.5F);
                }
                this.addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        linkedOrb.playAnimation(ATTACK_IMG, MED_ANIM);
                        this.isDone = true;
                    }
                });
                this.addToBot(new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.5F));
                this.addToBot(new VFXAction(new SmallLaserEffect(target.hb.cX, target.hb.cY, linkedOrb.getXPosition(), linkedOrb.getYPosition()), 0.0F));
                DamageInfo di = new DamageInfo(owner, damage, DamageInfo.DamageType.THORNS);
                DontLoopcast.orbAttack.set(di, true);
                this.addToBot(new DamageAction(target, di, AbstractGameAction.AttackEffect.NONE, true));
                linkedOrb.onLinkedPowerTrigger();
                int finalDamage = damage;
                this.addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        linkedOrb.evokeAmount += finalDamage;
                        linkedOrb.updateDescription();
                        this.isDone = true;
                    }
                });
            }
        }

        @Override
        public int onAttacked(DamageInfo info, int damageAmount) {
            if (info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS) {
                this.addToTop(new AbstractGameAction() {
                    @Override
                    public void update() {
                        linkedOrb.playAnimation(HURT_IMG, MED_ANIM);
                        this.isDone = true;
                    }
                });
            }
            return damageAmount;
        }

        @Override
        public AbstractPower makeCopy() {
            return new BitOrbPower(linkedOrb);
        }
    }

    @SpirePatch(clz = DamageInfo.class, method = SpirePatch.CLASS)
    public static class DontLoopcast {
        public static SpireField<Boolean> orbAttack = new SpireField<>(() -> Boolean.FALSE);
    }
}
