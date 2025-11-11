import React, { useState } from 'react';
import { useAuth } from './AuthContext';
import { Container, Card, CardHeader, Col, Row, Button, FormGroup, Input, Label } from 'reactstrap';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await login(username, password);
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <Container>
            <Row>
                <Col sm={{ size: 6, offset: 3 }} style={{ marginTop: '50px' }}>
                    <Card>
                        <CardHeader><strong>Login</strong></CardHeader>
                        <form onSubmit={handleSubmit} style={{ padding: '20px' }}>
                            <FormGroup>
                                <Label for='username'>Username:</Label>
                                <Input
                                    type='text'
                                    id='username'
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                />
                            </FormGroup>
                            <FormGroup>
                                <Label for='password'>Password:</Label>
                                <Input
                                    type='password'
                                    id='password'
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </FormGroup>
                            {error && <p style={{ color: 'red' }}>{error}</p>}
                            <Button color="primary" type="submit">Login</Button>
                        </form>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default LoginPage;