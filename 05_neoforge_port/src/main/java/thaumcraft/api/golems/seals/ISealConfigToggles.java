package thaumcraft.api.golems.seals;

public interface ISealConfigToggles {

    SealToggle[] getToggles();

    void setToggle(int index, boolean value);

    class SealToggle {
        public boolean value;
        public final String key;
        public final String name;

        public SealToggle(boolean value, String key, String name) {
            this.value = value;
            this.key = key;
            this.name = name;
        }

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }
    }
}
