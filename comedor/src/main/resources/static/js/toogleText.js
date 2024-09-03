function toggleText() {
    var textElement = document.getElementById("thymeleafText");
    if (textElement.style.display === "none") {
        textElement.style.display = "block";
    } else {
        textElement.style.display = "none";
    }
}