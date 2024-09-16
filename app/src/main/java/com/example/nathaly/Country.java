package com.example.nathaly;

public class Country {
    private Flags flags;

    public String getFlagUrl() {
        return flags != null ? flags.getPng() : null;
    }

    public class Flags {
        private String png;

        public String getPng() {
            return png;
        }
    }
}


