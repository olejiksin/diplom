import React from 'react';
import {Database} from '../types/types';

interface Props {
    databases: Database[];
    removeDB: (id: string) => void;
    startOrStopDB: (id: string) => void;
}

export const DbList: React.FC<Props> = (props) => {

    const {databases} = props;

    const deleter = (e: React.SyntheticEvent<HTMLButtonElement>, id: string): void => {
        let decision = window.confirm('Точно удалить?');
        if (decision) {
            props.removeDB(id);
        }
    };
    const startOrDown = (e: React.SyntheticEvent<HTMLButtonElement>, id: string) => {
        props.startOrStopDB(id);
    }
    const list = databases[0] !== undefined ?
        (databases.map((db: Database, key: number) => {
            return (
                <tr key={key}>
                    <td>{db.dbType}</td>
                    <td>{db.ip}</td>
                    <td>{db.port}</td>
                    <td>{db.status}</td>
                    <td>
                        <button className={`btn ${db.status === 'online' ? 'btn-delete_stop' : 'btn-start'}`}
                                onClick={(e) => {
                                    startOrDown(e, db.id)
                                }}>{db.status === 'online' ? 'Stop' : 'Start'}</button>
                        <button className="btn btn-delete_stop" onClick={(e) => deleter(e, db.id)}>Delete app</button>
                    </td>
                </tr>
            )
        }))
        : null;

    return (
        <div>
            {props.databases.length > 0 ?
                <table className="appList">
                    {/*<thead className={'db-thead'}>*/}
                    {/*/!*<tr>*!/*/}
                    {/*    <td>Db Type</td>*/}
                    {/*    <td>Ip</td>*/}
                    {/*    <td>Port</td>*/}
                    {/*    <td>Status</td>*/}
                    {/*/!*</tr>*!/*/}
                    {/*</thead>*/}
                    {/*<tr>*/}
                    {/*    <td>Db Type</td>*/}
                    {/*    <td>Ip</td>*/}
                    {/*    <td>Port</td>*/}
                    {/*    <td>Status</td>*/}
                    {/*    <td>*/}
                    {/*        <button className={'btn btn-start'}>Start</button>*/}
                    {/*        <button className="btn btn-delete_stop">Delete app</button>*/}
                    {/*    </td>*/}
                    {/*</tr>*/}
                    <tbody>
                    {list}
                    </tbody>
                </table>
                : <h2 className={'oops'}>You dont have any databases. You can add database in "Add new db" tab.</h2>
            }
        </div>
    )
}
