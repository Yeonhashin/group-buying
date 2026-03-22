import { useEffect } from 'react';
import api from './api/axios';

function App() {

    useEffect(() => {
        api.get('/health')
            .then(res => console.log(res.data))
            .catch(err => console.error(err));
    }, []);

    return <h1>Group Buying</h1>;
}

export default App;