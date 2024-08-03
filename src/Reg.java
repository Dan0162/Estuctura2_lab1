class Reg {
    int ID;
    String name;

    public Reg(int newID, String newName) {
        this.ID = newID;
        this.name = newName;
    }

    @Override
    public String toString() {
        return "(" + ID + ", " + name + ")";
    }

}