import { useState } from 'react'
import axios from 'axios'

function AddGroup() {
    const [fname, setFname] = useState('')
    const [description, setDescription] = useState('')

    const handleSubmit = async (e) => {
        e.preventDefault()

        try {
            await axios.post('http://localhost:8080/api/groups', {
                fname,
                description,
            })
            alert('Group created!')
            setFname('')
            setDescription('')
        } catch (err) {
            alert('Error creating group')
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>Add Group</h2>
            <input
                type="text"
                placeholder="Group name"
                value={fname}
                onChange={(e) => setFname(e.target.value)}
                required
            />
            <textarea
                placeholder="Description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
            />
            <button type="submit">Add Group</button>
        </form>
    )
}

export default AddGroup