package uz.jagito.jchat.models

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty",

    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",
    var fileUrl:String = "empty",

    var lastMessage:String = ""

) {
    override fun equals(other: Any?): Boolean {
        return id == (other as CommonModel).id
    }
}