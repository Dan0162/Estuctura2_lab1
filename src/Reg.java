public class Reg implements Comparable<Reg> {
    int ID;
    String data;

    public Reg(int ID, String data) {
        this.ID = ID;
        this.data = data;
    }

    @Override
    public int compareTo(Reg other) {
        return Integer.compare(this.ID, other.ID);
    }
}