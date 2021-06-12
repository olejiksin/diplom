import React from 'react';
import ReactDOM from 'react-dom';
import './css/index.scss';
import './css/login_reg.scss';
import './css/main.scss';
import './css/help_page.scss';
import './css/loader.scss';
import './css/app_server_list.scss';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import {storeGlob} from "./storeConf";
import {Provider} from "react-redux";
import Login from './containers/login'
import {RegPage} from './components/regPage'
import MainPage from './containers/mainPage';
import HelpPage from './containers/helpPage';

const App = () => (
    <Switch>
        <Route component={Login}  path={'/login/'}/>
        <Route component={HelpPage} path={'/help/'}/>
        <Route component={RegPage}  path={'/reg/'}/>
        <Route component={MainPage}  path={'/main/:login/'}/>
    </Switch>
);

ReactDOM.render(
    <Provider store={storeGlob}>
        <Router>
            {/*<React.StrictMode>*/}
            <App/>
            {/*</React.StrictMode>*/}
        </Router>
    </Provider>
    , document.getElementById('root')
);

