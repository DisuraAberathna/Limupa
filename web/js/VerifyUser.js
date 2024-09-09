const verifyUser = async() => {
    const reqObject = {
        otp: document.getElementById("otp").value
    };

    try {
        const response = await fetch(
                "UserVerification",
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
                    text: data.msg,
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
                if (data.msg === "Verification unavailable please sign in") {
                    setTimeout(() => {
                        window.location.href = "userSignin.html";
                    }, 3000);
                }
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const resendOTP = async() => {
    try {
        const response = await fetch("UserResendOTP");

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "OTP was resended! Please check your inbox.",
                    icon: "success"
                });
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
                if (data.msg === "Verification unavailable please sign in") {
                    setTimeout(() => {
                        window.location.href = "userSignin.html";
                    }, 3000);
                }
            }

        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }

};