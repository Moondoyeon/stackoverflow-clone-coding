import Nav from '../../components/Nav';
import MyContent from '../../components/MyContent';
import MyInfo from '../../components/MyInfo';
import styled from 'styled-components';
import { useState, useEffect } from 'react';
import axios from 'axios';
import { getLoginCookie } from '../../lib/cookie';
import { useNavigate } from 'react-router-dom';
import MyFooter from '../../components/MyFooter';

const MyPage = () => {
  const navigate = useNavigate();
  const [nickname, setNickname] = useState('');
  const [location, setLocation] = useState('');
  const [about, setAbout] = useState('');
  const [answers, setAnswers] = useState([]);
  const [questions, setQuestions] = useState([]);
  const [infoData, setInfoData] = useState({});
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem('token');
  // 파라미터는 profile_id 로 받기.
  useEffect(() => {
    if (token) {
      axios
        .get(`http://3.39.180.45:56178/DBtest/getProfile`, {
          headers: { Authorization: getLoginCookie() },
        })
        .then((res) => {
          setInfoData(res.data);
          setNickname(res.data.displayname);
          setLocation(res.data.location);
          setAbout(res.data.about);
          axios
            .get(
              `http://3.39.180.45:56178/DBtest/findPost/${res.data.profile_id}?page=1`,
              {
                headers: { Authorization: getLoginCookie() },
              }
            )
            .then((res) => {
              console.log(res.data.posts);
              setQuestions(res.data.posts);
            })
            .catch(() => {
              console.log('findpost error from mypage');
            });
          axios
            .get(
              `http://3.39.180.45:56178/DBtest/findAnswersBymember/${res.data.profile_id}?page=1&size=15`,
              {
                headers: { Authorization: getLoginCookie() },
              }
            )
            .then((res) => {
              console.log(res.data.answers);
              setAnswers(res.data.answers);
              setLoading(false);
            });
        })
        .catch((err) => {
          navigate('/');
          if (err.response.status === 500) return navigate('/mypage');
        });
    }
  }, [token]);
  if (loading) return null;

  return (
    <Container>
      <Main>
        <Nav />
        <MypageContainer>
          <MyInfo
            nickname={infoData.displayname}
            location={infoData.location}
            signupDate={infoData.sign_in_date}
          />
          <MyContent
            reputation={infoData.stub_reputation}
            reached={infoData.stub_reached}
            answers={answers}
            questions={questions}
            about={about}
            setAbout={setAbout}
            nickname={nickname}
            setNickname={setNickname}
            location={location}
            setLocation={setLocation}
            userId={infoData.profile_id}
          />
        </MypageContainer>
      </Main>
      <MyFooter />
    </Container>
  );
};

export default MyPage;

const Container = styled.div`
  display: flex;
  flex-direction: column;
`;
const Main = styled.div`
  display: flex;
`;
const MypageContainer = styled.div`
  display: flex;
  flex-direction: column;
`;
