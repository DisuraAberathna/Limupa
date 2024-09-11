const handlePress = async() => {
    const reqObject = {
        f_name: document.getElementById("f_name").value,
        l_name: document.getElementById("l_name").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        confirm_password: document.getElementById("confirm_password").value
    };

    try {
        const response = await fetch(
                "UserRegistration",
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
                    text: "You are successfully registerd! Please check your inbox to verify your account.",
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