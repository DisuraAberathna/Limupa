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

const signout = async () => {
    try {
        const response = await fetch("Signout");

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