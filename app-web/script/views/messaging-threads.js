var TableView = require("./abstract/table");

var MessagingThreadsView = TableView.extend({
    /**
     * @override TableView
     */
    getHead: function() {
        return [
            [
                "Number",
                "Message"
            ]
        ];
    },

    /**
     * @override TableView
     */
    getRow: function(row) {
        return [
            '<a href="' + row.viewUrl() + '">' + row.get("address") + "</a>",
            row.get("body")
        ];
    }
});

module.exports = MessagingThreadsView;
