import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, CardHeader, CardBody, FormGroup, Label, Input } from 'reactstrap';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import * as deviceApi from '../device/device-api';
import * as monitoringApi from './monitoring-api';
import APIResponseErrorMessage from '../commons/errorhandling/api-response-error-message';

const MonitoringPage = () => {
    const [devices, setDevices] = useState([]);
    const [selectedDeviceId, setSelectedDeviceId] = useState('');
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]); // Default to today
    const [chartData, setChartData] = useState([]);
    const [error, setError] = useState(null);
    const [errorStatus, setErrorStatus] = useState(0);

    // Load devices on mount
    useEffect(() => {
        fetchDevices();
    }, []);

    // Reload chart when device or date changes
    useEffect(() => {
        if (selectedDeviceId && selectedDate) {
            fetchConsumptionData();
        }
    }, [selectedDeviceId, selectedDate]);

    const fetchDevices = () => {
        // Only fetch devices belonging to the logged-in user
        deviceApi.getMyDevices((result, status, err) => {
            if (result !== null && status === 200) {
                setDevices(result);
                // Auto-select the first device if available
                if (result.length > 0) {
                    setSelectedDeviceId(result[0].id);
                }
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const fetchConsumptionData = () => {
        monitoringApi.getHourlyConsumption(selectedDeviceId, selectedDate, (result, status, err) => {
            if (result !== null && status === 200) {
                // Transform backend data (DTO) to Recharts format
                const data = result.map(item => {
                    // hourTimestamp comes as "2025-11-24T10:00:00"
                    const dateObj = new Date(item.hour);
                    return {
                        hour: dateObj.getHours(), // Extract hour (0-23)
                        energy: item.consumption   // kWh
                    };
                });
                
                // Sort by hour to ensure the line chart connects correctly
                data.sort((a, b) => a.hour - b.hour);
                
                setChartData(data);
                setError(null);
                setErrorStatus(0);
            } else {
                console.error("Error fetching consumption:", err);
                setChartData([]);
                // Don't show error alert for empty data, just empty chart
                if (status !== 404) {
                    setErrorStatus(status);
                    setError(err);
                }
            }
        });
    };

    return (
        <Container>
            <CardHeader>
                <strong>My Energy Consumption</strong>
            </CardHeader>
            <Card>
                <CardBody>
                    <Row>
                        <Col md={6}>
                            <FormGroup>
                                <Label for="deviceSelect">Select Device:</Label>
                                <Input 
                                    type="select" 
                                    name="device" 
                                    id="deviceSelect" 
                                    value={selectedDeviceId}
                                    onChange={(e) => setSelectedDeviceId(e.target.value)}
                                >
                                    {devices.length === 0 && <option>No devices found</option>}
                                    {devices.map(device => (
                                        <option key={device.id} value={device.id}>
                                            {device.deviceName}
                                        </option>
                                    ))}
                                </Input>
                            </FormGroup>
                        </Col>
                        <Col md={6}>
                            <FormGroup>
                                <Label for="dateSelect">Select Date:</Label>
                                <Input 
                                    type="date" 
                                    name="date" 
                                    id="dateSelect" 
                                    value={selectedDate}
                                    onChange={(e) => setSelectedDate(e.target.value)}
                                />
                            </FormGroup>
                        </Col>
                    </Row>
                    
                    <div style={{ width: '100%', height: 400, marginTop: '20px' }}>
                        <h5 style={{textAlign: 'center'}}>Hourly Consumption (kWh)</h5>
                         {chartData.length > 0 ? (
                            <ResponsiveContainer>
                                <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="hour" label={{ value: 'Hour (0-23)', position: 'insideBottomRight', offset: -5 }} />
                                    <YAxis label={{ value: 'Energy (kWh)', angle: -90, position: 'insideLeft' }} />
                                    <Tooltip />
                                    <Legend />
                                    <Line type="monotone" dataKey="energy" stroke="#8884d8" activeDot={{ r: 8 }} strokeWidth={2} />
                                </LineChart>
                            </ResponsiveContainer>
                        ) : (
                            <p className="text-center" style={{marginTop: '50px', color: 'gray'}}>
                                No consumption data available for this device on this date.
                            </p>
                        )}
                    </div>

                    {errorStatus > 0 && (
                        <APIResponseErrorMessage errorStatus={errorStatus} error={error} />
                    )}
                </CardBody>
            </Card>
        </Container>
    );
};

export default MonitoringPage;