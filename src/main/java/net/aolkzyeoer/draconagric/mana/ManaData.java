package net.aolkzyeoer.draconagric.mana;

public class ManaData {

    private int mana;
    private int maxMana;

    public ManaData(int maxMana){
        this.maxMana = maxMana;
        this.mana = maxMana;
    }
    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
        this.mana = Math.min(mana, maxMana);
    }

    public boolean consume(int amount) {
        if (mana < amount) return false;
        mana -= amount;
        return true;
    }

    //渐渐加速
    public void regen() {
        int missing = maxMana - mana;
        if (missing <= 0) return;

        int regen = 1 + (missing / 20);
        mana = Math.min(maxMana, mana + regen);
    }
}

