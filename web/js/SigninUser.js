const handlePress = async() => {
    const reqObject = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        remember_me: document.getElementById("remember_me").checked
    };

    try {
        const response = await fetch(
                "UserSignIn",
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
                    text: "You are successfully sign in!",
                    icon: "success"
                });
                setTimeout(() => {
                    window.location.href = "index.html";
                }, 3000);
            } else {
                if (data.msg === "Not Verified") {
                    Swal.fire({
                        title: "Warning",
                        text: "Your account not verified! please verify.",
                        icon: "warning"
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
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};