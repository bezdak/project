package sk.tuke.oop.aliens;

import sk.tuke.oop.aliens.actor.AbstractActor;
import sk.tuke.oop.framework.Animation;

public class Reactor extends AbstractActor {

    private int temp;
    private int dmg;
    private int maxTemp;
    private int hotTemp;
    private int safeTemp;
    private int yearOfProduction;
    // variable for temp -> dmg conversion
    private int divideBy;
    private boolean isBroken;
    private String manufacturer;
    private static final Animation normalAnim = new Animation("images/reactor_on.png", 80, 80, 100);
    private static final Animation hotAnim = new Animation("images/reactor_hot.png", 80, 80, 50);
    private static final Animation brokenAnim = new Animation("images/reactor_broken.png", 80, 80, 100);

    public Reactor() {
        reactorInit();
    }

    public Reactor(String newManu, int newYearOfProd) {
        setManufacturer(newManu);
        setYearOfProduction(newYearOfProd);
        reactorInit();
    }

    private void reactorInit() {
        setTemp(0);
        setDmg(0);
        setSafeTemp(2000); // after this temp dmg starts to increase
        setHotTemp(4000);
        setMaxTemp(6000); // temp of reactor breaking down
        divideBy = (getMaxTemp() - getSafeTemp()) / 100;
        setIsBroken(false);
        // animation will continue back ( 1 => 2 => 3 => 2 => 1)
        // instead of repeating ( 1 => 2 => 3 => 1 => 2)
        normalAnim.setPingPong(true);
        hotAnim.setPingPong(true);

        updateAnim();
    }

    public int getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(int yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getTemp() {
        return this.temp;
    }

    public void setTemp(int newTemp) {
        this.temp = newTemp;
    }

    public int getSafeTemp() {
        return this.safeTemp;
    }

    public void setSafeTemp(int newSafeTemp) {
        this.safeTemp = newSafeTemp;
    }

    public boolean getIsBroken() {
        return this.isBroken;
    }

    public void setIsBroken(boolean newIsBroken) {
        this.isBroken = newIsBroken;
    }

    public int getMaxTemp() {
        return this.maxTemp;
    }

    public void setMaxTemp(int newMaxTemp) {
        this.maxTemp = newMaxTemp;
    }

    public int getHotTemp() {
        return this.hotTemp;
    }

    public void setHotTemp(int newHotTemp) {
        this.hotTemp = newHotTemp;
    }

    public int getDmg() {
        return this.dmg;
    }

    public void setDmg(int newDmg) {
        this.dmg = newDmg;
        if (this.dmg > 100) this.dmg = 100; // dmg can't be higher that 100%
        if (this.dmg == 100) setIsBroken(true);
    }

    public boolean isServiceNeeded() {
        if (getDmg() > 50 && getTemp() > 3000) return true;
        return false;
    }

    public void repairWith (Hammer hammer) {
        // repairing works with hammer only
        if (hammer == null) return;
        // repairing only damaged, but not destroyed reactor
        if (getDmg() == 0 || isBroken) return;

        // temporary variable for lowering temperature
        int dmgRed = getDmg() - 50;

        if (getDmg() - 50 < 0) setDmg(0);
        else setDmg(getDmg() - 50);
        hammer.use();

        // reducing temperature
        int newTemp = dmgRed * divideBy + 2000;
        if (getTemp() > newTemp) setTemp(newTemp);

        updateAnim();
    }

    public void incTemp(int increment) {
        // no increase after breaking or increment being negative
        if (getIsBroken() || increment < 0) return;
        // at 33%-66% dmg increase is 1.5x, over 66% it's 2x
        double incRate = 1;
        if (getDmg() < 33) incRate = 1;
        else if (33 <= getDmg() && getDmg() <= 66) incRate = 1.5;
        else if (66 < getDmg()) incRate = 2;
        // increase
        setTemp((int) Math.ceil(getTemp() + (increment * incRate)));

        // increasing dmg alongside with temp increase
        int increasedTemp = (getTemp() - getSafeTemp()) / divideBy;
        if (getTemp() > getSafeTemp() && (increasedTemp) > getDmg()) {
            setDmg(increasedTemp);
        }

        updateAnim();
    }

    public void decreaseTemp(int decrement) {
        // no decrease after breaking
        if (getIsBroken() || decrement < 0) return;
        // 50%+ => only halved decreasing
        double decRate = 1;
        if (getDmg() < 50) decRate = 1;
        else if (50 <= getDmg() && getDmg() < 100) decRate = 0.5;
        // decrease
        double decreasedTemp = Math.ceil(getTemp() - (decrement * decRate));
        setTemp((int) decreasedTemp);

        updateAnim();
    }

    private void updateAnim() {
        boolean normalPreset = getTemp() < getHotTemp(); // temp < 4000 - normal animation
        boolean hotPreset = getHotTemp() <= getTemp(); // temp > 4000 - hot animation
        if (normalPreset && getAnimation() != normalAnim && !getIsBroken()) { // normal
            setAnimation(normalAnim);
        } else if (hotPreset && getAnimation() != hotAnim && !getIsBroken()) { // hot
            setAnimation(hotAnim);
        } else if (getIsBroken() && getAnimation() != brokenAnim) { // broken
            setAnimation(brokenAnim);
        }
        if (hotPreset) {
            // the hotter it gets, the faster it pulses
            double durDecrement = (getTemp() - getHotTemp()) * 0.0125;
            hotAnim.setDuration((int) (50 - durDecrement));
        }
    }
}