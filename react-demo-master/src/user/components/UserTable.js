// src/user/components/UserTable.js
import React from "react";
import Table from "../../commons/tables/table";
import { Button } from "reactstrap"; // Import Button

// --- MODIFIED: Add columns as a prop ---
const UserTable = (props) => {

    // --- MODIFIED: Define columns here, including Actions ---
    const columns = [
        {
            Header: 'Name',
            accessor: 'fullName', // Use 'fullName' from PersonDTO
        },
        {
            Header: 'Email', // Add Email
            accessor: 'email',
        },
        {
            Header: 'Actions',
            Cell: (cellProps) => (
                <div>
                    <Button
                        color="info"
                        size="sm"
                        onClick={() => props.onEdit(cellProps.original)}
                    >
                        Edit
                    </Button>{' '}
                    <Button
                        color="danger"
                        size="sm"
                        onClick={() => props.onDelete(cellProps.original.id)}
                    >
                        Delete
                    </Button>
                </div>
            )
        }
    ];

    const filters = [
        {
            accessor: 'fullName',
        }
    ];

    return (
        <Table
            data={props.tableData} // Use props.tableData
            columns={columns}      // Use the columns defined above
            search={filters}
            pageSize={5}
        />
    );
};

export default UserTable;