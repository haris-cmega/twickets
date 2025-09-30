import { useEffect, useState } from 'react'
import axios from 'axios'

export default function GroupList() {
    const [groups, setGroups] = useState([])
    const [loading, setLoading] = useState(true)
    const [editId, setEditId] = useState(null)
    const [editFname, setEditFname] = useState('')
    const [editDescription, setEditDescription] = useState('')

    const fetchGroups = async () => {
        try {
            const res = await axios.get('http://localhost:8080/api/groups')
            setGroups(res.data)
        } catch (err) {
            console.error('Failed to fetch groups:', err)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchGroups()
    }, [])

    const handleDelete = async (id) => {
        try {
            await axios.delete(`http://localhost:8080/api/groups/${id}`)
            setGroups((prev) => prev.filter((g) => g.id !== id))
        } catch (err) {
            alert('Failed to delete group')
        }
    }

    const handleEdit = (group) => {
        setEditId(group.id)
        setEditFname(group.fname)
        setEditDescription(group.description)
    }

    const handleUpdate = async () => {
        try {
            await axios.put(`http://localhost:8080/api/groups/${editId}`, {
                fname: editFname,
                description: editDescription,
            })
            setEditId(null)
            fetchGroups()
        } catch (err) {
            alert('Failed to update group')
        }
    }

    if (loading) return <p>Loading groups...</p>

    return (
        <div>
            <h2>Group List</h2>
            <ul>
                {groups.map((group) => (
                    <li key={group.id}>
                        {editId === group.id ? (
                            <>
                                <input
                                    type="text"
                                    value={editFname}
                                    onChange={(e) => setEditFname(e.target.value)}
                                />
                                <input
                                    type="text"
                                    value={editDescription}
                                    onChange={(e) => setEditDescription(e.target.value)}
                                />
                                <button onClick={handleUpdate}>Save</button>
                                <button onClick={() => setEditId(null)}>Cancel</button>
                            </>
                        ) : (
                            <>
                                <strong>{group.fname}</strong> — {group.description}
                                <button onClick={() => handleEdit(group)}>Edit</button>
                                <button onClick={() => handleDelete(group.id)}>Delete</button>
                            </>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    )
}