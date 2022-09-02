import { useEffect, useState } from 'react';
import { Routes, Route } from 'react-router-dom';
import './App.css';
import MyHeader from './components/MyHeader';
import HeaderModal from './components/HeaderModal';
import MyPage from './pages/MyPage/MyPage';
import EditQuestion from './pages/Question/EditQuestion';
import PostQuestion from './pages/Question/PostQuestion';
import QuestionDetail from './pages/Question/QuestionDetail';
import QuestionList from './pages/Question/QuestionList';
import Login from './pages/Sign/Login';
import Logout from './pages/Sign/Logout';
import SignUp from './pages/Sign/SignUp';
import axios from 'axios';
import { useDispatch } from 'react-redux';
// import { getLoginCookie } from './lib/cookie';
import { setSignState } from './action/action';
import RequireAuth from './components/RequireAuth';

axios.defaults.withCredentials = false;
function App() {
  const dispatch = useDispatch();
  // const state = useSelector((state) => state.signInReducer);
  const [viewModal, setModal] = useState(false);
  const token = localStorage.getItem('token');
  useEffect(() => {
    if (token) {
      dispatch(setSignState(true));
    }
    // async () => {
    //   const res = await axios.get(
    //     `http://3.39.180.45:56178/DBtest/tokenLogin`,
    //     {
    //       headers: { authorization: getLoginCookie() },
    //     }
    //   );
    //   dispatch(setSignState(res.data.msg));
    //   delete res.data.msg;
    //   dispatch(setUserData(res.data));
    //   console.log(state.loginState);
    // };
  }, []);

  return (
    <div className="App">
      <MyHeader viewModal={viewModal} setModal={setModal} />
      {viewModal ? (
        <HeaderModal viewModal={viewModal} setModal={setModal} />
      ) : null}
      <Routes>
        <Route path="/" element={<QuestionList />} />
        <Route
          path="/login"
          element={
            <RequireAuth option={false}>
              <Login />
            </RequireAuth>
          }
        />
        <Route
          path="/logout"
          element={
            <RequireAuth option={true}>
              <Logout />
            </RequireAuth>
          }
        />
        <Route
          path="/signup"
          element={
            <RequireAuth option={false}>
              <SignUp />
            </RequireAuth>
          }
        />
        <Route
          path="/mypage"
          element={
            <RequireAuth option={true}>
              <MyPage />
            </RequireAuth>
          }
        />
        <Route path="/questiondetail/:id" element={<QuestionDetail />} />
        <Route
          path="/questionedit/:id"
          element={
            <RequireAuth option={true}>
              <EditQuestion />
            </RequireAuth>
          }
        />
        <Route
          path="/questionwrite"
          element={
            <RequireAuth option={true}>
              <PostQuestion />
            </RequireAuth>
          }
        />
      </Routes>
    </div>
  );
}

export default App;
