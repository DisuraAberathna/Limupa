const hasDigit = (event) => {
    if (event.key >= "0" && event.key <= "9") {
        return false;
    }
    return true;
};

const isDouble = (input) => {
    const value = input.value;

    const regex = /^\-?\d+(\.\d{0,2})?$/;

    if (!regex.test(value)) {
        input.value = value.slice(0, -1);
    }
};

const hasLetter = (event) => {
    let charCode = event.which ? event.which : event.keyCode;

    if (charCode < 48 || charCode > 57) {
        return false;
    }
    return true;
};

const checkSignedIn = async() => {
    try {
        const response = await fetch("CheckSignedIn");

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                document.getElementById("signedinView").style.display = "block";
                document.getElementById("notSignedinView").style.display = "none";
            } else {
                document.getElementById("signedinView").style.display = "none";
                document.getElementById("notSignedinView").style.display = "block";
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

document.addEventListener("DOMContentLoaded", (checkSignedIn()));

const signout = async () => {
    try {
        const response = await fetch("Signout");

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                window.location.reload();
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const addToCart = async(id, qty) => {
    try {
        const response = await fetch(
                "AddToCart?id=" + id + "&qty=" + qty
                );

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "Product added to the cart!",
                    icon: "success"
                });
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const addToWatchlist = async(id) => {
    try {
        const response = await fetch(
                "AddToWatchlist?id=" + id
                );

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "Product added to the watchlist!",
                    icon: "success"
                });
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};