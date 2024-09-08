const hasLetter = (event) => {
    if (event.charCode >= 48 && event.charCode <= 57) {
        return false;
    }
    return true;
};