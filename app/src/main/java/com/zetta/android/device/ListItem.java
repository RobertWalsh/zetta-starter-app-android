package com.zetta.android.device;

interface ListItem {

    int TYPE_HEADER_ACTION = 0;
    int TYPE_ACTIONS = 1;

    int getType();

    class HeaderActionListItem implements ListItem {

        private final String title;

        public HeaderActionListItem(String title) {
            this.title = title;
        }

        @Override
        public int getType() {
            return TYPE_HEADER_ACTION;
        }

        public String getTitle() {
            return title;
        }
    }

    class ActionListItem implements ListItem {

        private final String label;
        private final String action;

        public ActionListItem(String label, String action) {
            this.label = label;
            this.action = action;
        }

        @Override
        public int getType() {
            return TYPE_ACTIONS;
        }

        public String getLabel() {
            return label;
        }

        public String getAction() {
            return action;
        }
    }
}