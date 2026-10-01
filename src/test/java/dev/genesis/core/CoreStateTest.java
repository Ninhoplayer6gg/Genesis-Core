package dev.genesis.core;
import dev.genesis.species.SpeciesRegistry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class CoreStateTest {
    CoreState installed(){var s=new CoreState();s.install();return s;}
    @Test void newCoreStartsWithOneRealMatrix(){var s=installed();assertEquals(1,s.unlocked.size());assertTrue(s.unlocked.contains("adaptaris"));assertEquals(100,s.energy);}
    @Test void unlockRequiresBothDiscoveryAndLevel(){var s=installed();s.addXp(65);assertFalse(s.unlocked.contains("ferronox"));s.discover("metal");assertTrue(s.unlocked.contains("ferronox"));assertFalse(s.unlocked.contains("colonyx"));}
    @Test void colonyNeedsItsOwnScan(){var s=installed();s.addXp(200);s.discover("metal");assertFalse(s.unlocked.contains("colonyx"));s.discover("colony");assertTrue(s.unlocked.contains("colonyx"));}
    @Test void repeatedScanCannotFarmXp(){var s=installed();assertTrue(s.discover("metal"));int xp=s.xp;for(int i=0;i<50;i++)assertFalse(s.discover("metal"));assertEquals(xp,s.xp);}
    @Test void unimplementedSpeciesAlwaysRejected(){var s=installed();s.unlocked.add("veyl");assertFalse(s.denial("veyl",false,8).isEmpty());assertFalse(s.denial("../../bad",false,8).isEmpty());}
    @Test void recoverySurvivesRevertAndCannotBeShortened(){var s=installed();s.enter("adaptaris",false,8,180);s.pay(0,8,30);s.leave(10);assertEquals(180,s.transformationCooldown);assertEquals(30,s.cooldowns[0]);assertFalse(s.denial("adaptaris",false,8).isEmpty());}
    @Test void energyReachesZeroWithoutUnderflow(){var s=installed();s.enter("adaptaris",false,8,180);s.energy=.1;s.energySecond(1,1);assertEquals(0,s.energy);s.leave(180);assertFalse(s.transformed());}
    @Test void regenerationHasHardCap(){var s=installed();s.energy=99;s.energySecond(1,100);assertEquals(s.maxEnergy(),s.energy);}
    @Test void absoluteRequiresUnlockAndCorrectSpecies(){var s=installed();assertFalse(s.denial("adaptaris",true,8).isEmpty());s.absoluteUnlocked=true;assertEquals("",s.denial("adaptaris",true,8));s.unlocked.add("ferronox");assertFalse(s.denial("ferronox",true,8).isEmpty());}
    @Test void onlyTwoAdaptationSlotsAndNoInstantImmunity(){var s=installed();s.enter("adaptaris",false,8,1);s.expose("thermal",1000);assertTrue(s.mitigation("thermal")<.1);s.expose("impact",4);s.expose("cold",4);assertEquals(2,s.adaptations.size());for(int i=0;i<100;i++)s.expose("thermal",100);assertEquals(.5,s.mitigation("thermal"));}
    @Test void changingFormClearsAdaptationsButKeepsCooldowns(){var s=installed();s.enter("adaptaris",false,8,1);s.expose("impact",5);s.pay(2,10,300);s.enter("ferronox",false,8,1);assertTrue(s.adaptations.isEmpty());assertEquals(300,s.cooldowns[2]);}
    @Test void invalidSlotsAndPacketNumbersCannotCast(){var s=installed();s.enter("adaptaris",false,8,1);assertFalse(s.canCast(-1,1));assertFalse(s.canCast(5,1));s.abilityLock=0;s.energy=0;assertFalse(s.canCast(0,1));s.energy=Double.NaN;assertFalse(s.denial("ferronox",false,1).isEmpty());}
    @Test void abilityCostsAndTicksAreAuthoritative(){var s=installed();s.enter("adaptaris",false,8,2);s.abilityLock=0;assertTrue(s.canCast(0,8));s.pay(0,8,30);assertFalse(s.canCast(0,8));for(int i=0;i<30;i++)s.advanceTick();assertTrue(s.canCast(0,8));assertEquals(84,s.energy);}
    @Test void foreignPowerIdsAreNeverOwned(){assertFalse(SpeciesRegistry.ownedPower("other:adaptaris"));assertFalse(SpeciesRegistry.ownedPower("genesis:other_power"));assertTrue(SpeciesRegistry.ownedPower("genesis:adaptaris_absolute"));}
    @Test void xpDoesNotOverflow(){var s=installed();s.addXp(Integer.MAX_VALUE);assertEquals(1000000,s.xp);assertEquals(50,s.level());assertEquals(5,s.tier());}
}
