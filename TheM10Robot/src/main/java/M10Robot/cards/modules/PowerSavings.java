package M10Robot.cards.modules;

import M10Robot.M10RobotMod;
import M10Robot.cards.abstractCards.AbstractModuleCard;
import M10Robot.cards.interfaces.ModularDescription;
import M10Robot.characters.M10Robot;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static M10Robot.M10RobotMod.makeCardPath;

public class PowerSavings extends AbstractModuleCard implements ModularDescription {

    /*
     * Wiki-page: https://github.com/daviscook477/BaseMod/wiki/Custom-Cards
     *
     * Defend Gain 5 (8) block.
     */


    // TEXT DECLARATION

    public static final String ID = M10RobotMod.makeID(PowerSavings.class.getSimpleName());
    public static final String IMG = makeCardPath("PowerSavings.png");

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = M10Robot.Enums.GREEN_SPRING_CARD_COLOR;

    private static final int ENERGY = 1;
    private static final int UPGRADE_PLUS_ENERGY = 1;
    private static final int WEAK = 25;
    private static final int UPGRADE_PLUS_WEAK = -5;

    // /STAT DECLARATION/

    //TODO let this bounce under zero to become deal more damage?
    public PowerSavings() {
        super(ID, IMG, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = ENERGY;
        secondMagicNumber = baseSecondMagicNumber = WEAK;
        initializeDescription();
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}

    //Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            //upgradeMagicNumber(UPGRADE_PLUS_ENERGY);
            upgradeSecondMagicNumber(UPGRADE_PLUS_WEAK);
            if (baseSecondMagicNumber < 0) {
                baseSecondMagicNumber = 0;
                secondMagicNumber = baseSecondMagicNumber;
            }
            initializeDescription();
        }
    }

    @Override
    public void changeDescription() {
        if (DESCRIPTION != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(DESCRIPTION);
            for (int i = 0 ; i < magicNumber ; i++) {
                sb.append(EXTENDED_DESCRIPTION[1]);
            }
            sb.append(EXTENDED_DESCRIPTION[0]);
            rawDescription = sb.toString();
        }
    }
}
