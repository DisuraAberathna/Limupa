const loadAddress = async() => {
    try {
        const response = await fetch("LoadAddress");

        if (response.ok) {
            const data = await response.json();

            let addressHtml = document.getElementById("address-view");
            document.getElementById("address-view-main").innerHTML = "";
            data.addressList.forEach(address => {
                let addressCloneHtml = addressHtml.cloneNode(true);
                addressCloneHtml.querySelector("#is-used").checked = address.status === 1 ? true : false;
                addressCloneHtml.querySelector("#added-name").innerHTML = address.user.f_name + " " + address.user.l_name;
                addressCloneHtml.querySelector("#added-address").innerHTML = address.line_1 + ", " + address.line_2 + ", " + address.city.name + " - " + address.postal_code;
                addressCloneHtml.querySelector("#added-mobile").innerHTML = address.mobile;
                document.getElementById("address-view-main").appendChild(addressCloneHtml);
            });
            const selectTag = document.getElementById("city-select");
            data.cityList.forEach(item => {
                let optionTag = document.createElement("option");
                optionTag.value = item.id;
                optionTag.innerHTML = item.name;
                selectTag.appendChild(optionTag);    
            });
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};
const loadData = async() => {
    try {
        const response = await fetch("LoadCheckout");

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                let total = 0;
                let totalShipping = 0;
                const cartItemHtml = document.getElementById("cart-item");
                document.getElementById("cart-item-body").innerHTML = "";
                data.cartList.forEach(item => {
                    const itemSubTotal = item.product.price * item.qty;
                    totalShipping += item.product.shipping * item.qty;
                    total += itemSubTotal;
                    let cartItemCloneHtml = cartItemHtml.cloneNode(true);
                    cartItemCloneHtml.querySelector("#cart-product-name").innerHTML = item.product.title + " &times; " + item.qty;
                    cartItemCloneHtml.querySelector("#p-amount").innerHTML = "LKR " +
                            new Intl.NumberFormat(
                                    "en-US",
                                    {
                                        minimumFractionDigits: 2
                                    }

                            ).format((itemSubTotal));
                    document.getElementById("cart-item-body").appendChild(cartItemCloneHtml);
                });
                document.getElementById("shi-amount").innerHTML = "LKR " +
                        new Intl.NumberFormat(
                                "en-US",
                                {
                                    minimumFractionDigits: 2
                                }

                        ).format((totalShipping));
                document.getElementById("sub-amount").innerHTML = "LKR " +
                        new Intl.NumberFormat(
                                "en-US",
                                {
                                    minimumFractionDigits: 2
                                }

                        ).format((total));
                document.getElementById("amount").innerHTML = "LKR " +
                        new Intl.NumberFormat(
                                "en-US",
                                {
                                    minimumFractionDigits: 2
                                }

                        ).format((total + totalShipping));
            } else {
                if (data.msg === "empty") {
                    window.location.href = "index.html";
                } else {
                    Swal.fire({
                        title: "Warning",
                        text: data.msg,
                        icon: "warning"
                    });
                }
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};
const addAddress = async() => {
    const line_1 = document.getElementById("line_1");
    const line_2 = document.getElementById("line_2");
    const city = document.getElementById("city-select");
    const postal_code = document.getElementById("postal_code");
    const mobile = document.getElementById("mobile");
    const reqObject = {
        line_1: line_1.value,
        line_2: line_2.value,
        city: city.value,
        postal_code: postal_code.value,
        mobile: mobile.value
    };
    try {
        const response = await fetch(
                "AddAddress",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(reqObject)
                }
        );
        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "Shipping address changed!",
                    icon: "success"
                });

                line_1.value = "";
                line_2.value = "";
                city.value = 0;
                postal_code.value = "";
                mobile.value = "";
                loadAddress();
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

payhere.onCompleted = function onCompleted(orderId) {
    sendMail(orderId);
};

payhere.onDismissed = function onDismissed() {
    Swal.fire({
        title: "Warning",
        text: "Payment dismissed",
        icon: "warning"
    });
};

payhere.onError = function onError(error) {
    Swal.fire({
        title: "Error",
        text: error,
        icon: "error"
    });
};

var x = 0;

const checkout = async() => {
    try {
        const response = await fetch(
                "Checkout", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
        );

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                payhere.startPayment(data.payhereJson);
            } else {
                if (data.msg === "Order not found!" && x < 10) {
                    payhere.startPayment(data.payhereJson);
                } else {
                    Swal.fire({
                        title: "Warning",
                        text: data.msg,
                        icon: "warning"
                    });
                    x++;
                }
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const sendMail = async(id) => {
    console.log("sendMail: " + id);
    try {
        const response = await fetch("SendMail?id=" + id);

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "Payment completed! Your invoice send to your email.",
                    icon: "success"
                });
                setTimeout(() => {
                    window.location.href = "index.html";
                }, 3000);
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