let count = 0; // to increment total
let intervalId = setInterval(simulateLiveTransactions, 10000);

function simulateLiveTransactions() {
    fetch(`/live-transactions`)
        .then(res => {
            if (!res.ok) { throw new Error("Connection Lost") };
            return res.json();
        })
        .then(data => {
            if (!data || data.length === 0){ return };

            const tableBody = document.getElementById("transactions-table-body");
            const  total = document.getElementById("counter");

            data.forEach(transaction => {
                count += 1;
                displayTransaction(transaction);
            });
            total.innerHTML = count;
        })
        .catch(err => alert(err.message));
}


document.getElementById("searchBtn").addEventListener("click", (event) => { //handle search
    event.preventDefault();

    clearInterval(intervalId);
    const query = document.getElementById("transaction").value;

    fetch(`/search?q=${query}`)
        .then(res => {
            if (!res.ok) { throw new Error("Transaction not found!") };
            return res.json();
        })
        .then(data => {
            const tableBody = document.getElementById("transactions-table-body");
            const  total = document.getElementById("counter");
            tableBody.innerHTML = "";
            displayTransaction(data);
            total.innerHTML = 1;
        })
        .catch(err => alert(err.message));
});


function displayTransaction(tx) {
    const tableBody = document.getElementById("transactions-table-body");
    let status = "Default";
    if (tx.status === "Success") {
        status = "success";
    } else if (tx.status === "Failed") {
        status = "danger";
    } else if (tx.status === "Pending") {
        status = "warning"
    };

    const row = document.createElement("tr");
    console.log("status: " + status);
    row.className = status;
    row.innerHTML = `
        <td>${tx.id}</td>
        <td>${tx.payer}</td>
        <td>${tx.payee}</td>
        <td>${tx.description}</td>
        <td>${tx.paymentMethod}</td>
        <td>${tx.date}</td>
        <td>${tx.amount}</td>
        <td>${tx.status}</td>
    `;
    tableBody.prepend(row);
}

