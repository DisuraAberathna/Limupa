const handlePress = async() => {
    const reqObject = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        confirm_password: document.getElementById("confirm_password").value
    };

    try {
        const response = await fetch(
                "UserForgotPassword",
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
                    text: "OTP send! Please check your inbox.",
                    icon: "success"
                });
                setTimeout(() => {
                    window.location.href = "userVerify.html";
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