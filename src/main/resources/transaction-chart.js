document.addEventListener('DOMContentLoaded', function() {
    // Select all transaction divs
    const transactions = document.querySelectorAll('.transaction');

    // Exit if no transactions found
    if (!transactions.length) return;

    // Convert to array of transaction objects
    const transactionData = Array.from(transactions).map(transactionDiv => {
        return {
            timestamp: transactionDiv.querySelector('p:nth-child(1)').textContent.replace('Date:', '').trim(),
            id: transactionDiv.querySelector('p:nth-child(2)').textContent.replace('ID:', '').trim(),
            amount: parseFloat(transactionDiv.querySelector('p:nth-child(3)').textContent.replace('Amount:£', '').trim()),
            from: transactionDiv.querySelector('p:nth-child(4)').textContent.replace('From:', '').trim(),
            to: transactionDiv.querySelector('p:nth-child(5)').textContent.replace('To:', '').trim(),
            type: transactionDiv.querySelector('p:nth-child(6)').textContent.replace('Type:', '').trim(),
            status: transactionDiv.querySelector('p:nth-child(7)').textContent.includes('Accepted')
        };
    });

    // Count transactions by type
    const typeCounts = {
        'DEPOSIT': 0,
        'WITHDRAWAL': 0,
        'PAYMENT': 0,
        'TRANSFER': 0
    };

    transactionData.forEach(transaction => {
        if (typeCounts.hasOwnProperty(transaction.type)) {
            typeCounts[transaction.type]++;
        }
    });

    // Create container for the chart
    const chartContainer = document.createElement('div');
    chartContainer.className = 'chart-container';
    chartContainer.style.width = '400px';
    chartContainer.style.height = '400px';
    chartContainer.style.margin = '20px auto';

    // Create canvas for the chart
    const canvas = document.createElement('canvas');
    canvas.id = 'transactionPieChart';
    chartContainer.appendChild(canvas);

    // Add the chart container after the transactions section
    const transactionsSection = document.querySelector('.transactions-section');
    transactionsSection.appendChild(chartContainer);

    // Create the chart
    createPieChart(typeCounts);
});

function createPieChart(typeCounts) {
    // Load Chart.js from CDN
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/chart.js';
    script.onload = function() {
        const ctx = document.getElementById('transactionPieChart').getContext('2d');

        const data = Object.values(typeCounts);
        const labels = Object.keys(typeCounts);

        // Colors for each transaction type
        const backgroundColors = [
            'rgba(75, 192, 192, 0.7)',  // DEPOSIT
            'rgba(255, 99, 132, 0.7)',  // WITHDRAWAL
            'rgba(255, 205, 86, 0.7)',  // PAYMENT
            'rgba(54, 162, 235, 0.7)'   // TRANSFER
        ];

        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: backgroundColors,
                    borderColor: backgroundColors.map(color => color.replace('0.7', '1')),
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                    },
                    title: {
                        display: true,
                        text: 'Transaction Types'
                    }
                }
            }
        });
    };

    document.head.appendChild(script);
}