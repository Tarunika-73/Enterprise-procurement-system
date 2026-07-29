/**
 * Export utilities for Finance Module documents and reports.
 * Supports CSV export, Excel formatting, and Print/PDF document rendering.
 */

/**
 * Downloads standard JSON array of objects as a CSV file.
 * @param {Array<Object>} data 
 * @param {string} filename 
 */
export const exportToCSV = (data, filename = 'finance_report.csv') => {
  if (!data || !data.length) return;

  const headers = Object.keys(data[0]);
  const csvRows = [];

  // Header row
  csvRows.push(headers.join(','));

  // Data rows
  for (const row of data) {
    const values = headers.map((header) => {
      const val = row[header];
      const escaped = ('' + (val ?? '')).replace(/"/g, '""');
      return `"${escaped}"`;
    });
    csvRows.push(values.join(','));
  }

  const csvString = csvRows.join('\n');
  const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', filename);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

/**
 * Downloads data formatted for Microsoft Excel (.xls XML format).
 * @param {Array<Object>} data 
 * @param {string} filename 
 */
export const exportToExcel = (data, filename = 'finance_export.xls') => {
  if (!data || !data.length) return;

  const headers = Object.keys(data[0]);
  let tableHTML = '<table border="1"><thead><tr>';
  headers.forEach((h) => {
    tableHTML += `<th style="background-color:#1e3a8a; color:#ffffff; font-weight:bold;">${h}</th>`;
  });
  tableHTML += '</tr></thead><tbody>';

  data.forEach((row) => {
    tableHTML += '<tr>';
    headers.forEach((h) => {
      tableHTML += `<td>${row[h] ?? ''}</td>`;
    });
    tableHTML += '</tr>';
  });

  tableHTML += '</tbody></table>';

  const blob = new Blob([tableHTML], { type: 'application/vnd.ms-excel;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', filename);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

/**
 * Triggers document printing window for PDF / Print output.
 * @param {string} title 
 * @param {string} htmlContent 
 */
export const printDocument = (title, htmlContent) => {
  const printWindow = window.open('', '_blank', 'height=650,width=900');
  if (!printWindow) return;

  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <title>${title}</title>
        <style>
          body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 20px; color: #1e293b; }
          h1, h2, h3 { color: #1e3a8a; }
          table { width: 100%; border-collapse: collapse; margin-top: 15px; }
          th, td { border: 1px solid #cbd5e1; padding: 10px; text-align: left; font-size: 13px; }
          th { background-color: #f1f5f9; color: #0f172a; }
          .header { border-bottom: 2px solid #2563eb; padding-bottom: 10px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
          .footer { margin-top: 30px; font-size: 11px; text-align: center; color: #64748b; border-top: 1px solid #e2e8f0; padding-top: 10px; }
          .badge { padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; background: #e2e8f0; }
        </style>
      </head>
      <body>
        <div class="header">
          <div>
            <h1>${title}</h1>
            <p style="margin: 0; color: #64748b; font-size: 12px;">Enterprise Procurement System — Financial Services</p>
          </div>
          <div style="text-align: right; font-size: 12px;">
            <p>Generated: ${new Date().toLocaleDateString()}</p>
          </div>
        </div>
        ${htmlContent}
        <div class="footer">
          Confidential Document — Internal Use Only — Enterprise Procurement System
        </div>
      </body>
    </html>
  `);

  printWindow.document.close();
  printWindow.focus();
  setTimeout(() => {
    printWindow.print();
  }, 250);
};
