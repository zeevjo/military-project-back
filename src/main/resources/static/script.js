import {x} from '/test.js';

console.log('Testing', x);

// API service to handle CRUD operations
const ApiService = (() => {
    const baseURL = "http://localhost:8080";

    const request = async (url, method = "GET", body = null) => {
        const options = {
            method,
            headers: { "Content-Type": "application/json" },
        };
        if (body) options.body = JSON.stringify(body);

        const response = await fetch(`${baseURL}${url}`, options);
        if (!response.ok) throw new Error(`Request failed: ${response.statusText}`);
        return response.json();
    };

    return {
        getAllUsers: () => request("/api/users"),
        addUser: (user) => request("/api/users", "POST", user),
        updateUser: (user) => request("/api/users", "PUT", user),
        deleteUser: (id) => request(`/api/users?id=${id}`, "DELETE"),
    };
})();

// UI Logic
const App = (() => {
    const userForm = document.getElementById("user-form");
    const userIdInput = document.getElementById("user-id");
    const usernameInput = document.getElementById("username");
    const userList = document.getElementById("user-list");
    const messageDiv = document.getElementById("message");

    const showMessage = (message, type = "success") => {
        messageDiv.textContent = message;
        messageDiv.className = type;
        setTimeout(() => (messageDiv.textContent = ""), 3000);
    };

    const renderUsers = async () => {
        try {
            const users = await ApiService.getAllUsers();
            userList.innerHTML = users
                .map(
                    (user) => `
                <li>
                    ${user.username}
                    <div>
                        <button class="edit" data-id="${user.id}" data-username="${user.username}">Edit</button>
                        <button class="delete" data-id="${user.id}">Delete</button>
                    </div>
                </li>`
                )
                .join("");
        } catch (error) {
            showMessage("Failed to load users", "error");
        }
    };

    const handleFormSubmit = async (event) => {
        event.preventDefault();
        const id = userIdInput.value.trim();
        const username = usernameInput.value.trim();

        if (!username) {
            showMessage("Username is required", "error");
            return;
        }

        try {
            if (id) {
                await ApiService.updateUser({ id: parseInt(id), username });
                showMessage("User updated successfully");
            } else {
                await ApiService.addUser({ username });
                showMessage("User added successfully");
            }
            userForm.reset();
            renderUsers();
        } catch (error) {
            showMessage(error.message, "error");
        }
    };

    const handleListClick = async (event) => {
        const target = event.target;
        const id = target.dataset.id;

        if (target.classList.contains("edit")) {
            userIdInput.value = id;
            usernameInput.value = target.dataset.username;
        }

        if (target.classList.contains("delete")) {
            try {
                await ApiService.deleteUser(id);
                showMessage("User deleted successfully");
                renderUsers();
            } catch (error) {
                showMessage(error.message, "error");
            }
        }
    };

    const init = () => {
        userForm.addEventListener("submit", handleFormSubmit);
        userList.addEventListener("click", handleListClick);
        renderUsers();
    };

    return { init };
})();

// Initialize the app
document.addEventListener("DOMContentLoaded", App.init);