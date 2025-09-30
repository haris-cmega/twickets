import { useEffect, useState } from 'react'
import './App.css'
import AddGroup from './AddGroup.jsx'
import GroupList from './components/GroupList.jsx'

function App() {
    const [message, setMessage] = useState('')

    useEffect(() => {
        fetch('http://localhost:8080/api/test/public')
            .then(res => res.text())
            .then(data => setMessage(data))
            .catch(err => setMessage('Failed to fetch: ' + err.message))
    }, [])

    return (
        <div className="app-container">
            <h1>Connection Test</h1>
            <p>Backend says:</p>
            <pre>{message}</pre>

            <hr />
            <AddGroup />
            <hr />
            <GroupList />
        </div>
    )
}

export default App