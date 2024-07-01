import {Box, Divider, FormControlLabel, FormGroup, Grid, Paper, TextField} from '@mui/material';
import {useSnackbar} from 'notistack';
import {ReactNode, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {Page} from '../../../components/layout';
import {useAuthDispatch, useAuthState} from '../../../contexts';
import {ELocalStorageKeys} from '../../../global/enums.ts';
import {UserService} from '../../../services';
import {HalfRow, OneRow, QuarterRow, SaveButtonPanel} from '../../components';
import AvatarSelector from './AvatarSelector.tsx';
import Switch from '@mui/material/Switch';

export default function ProfileSettingPage() {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();
  const [openLoading, setOpenLoading] = useState(false);
  const { user } = useAuthState();
  const dispatch = useAuthDispatch();

  const [showAvatars, setShowAvatars] = useState(false);
  const [avatarUrl, setAvatarUrl] = useState<string>(user.avatarUrl);
  const [firstName, setFirstName] = useState<string>(user.firstName);
  const [lastName, setLastName] = useState<string>(user.lastName);
  const [profession, setProfession] = useState<string>(user.profession);
  const [email, setEmail] = useState<string>(user.email);
  const [phoneNum, setPhoneNum] = useState<string>(user.phoneNum);
  const [address, setAddress] = useState<string>(user.address);
  const [blog, setBlog] = useState<string>(user.blog);
  const [introduction, setIntroduction] = useState<string>(user.introduction);
  const [backgroundImg, setBackgroundImg] = useState<string>(user.backgroundImg);
  const [photoImg, setPhotoImg] = useState<string>(user.photoImg);

  const [articleModule, setArticleModule] = useState<boolean>(user.features.articleModule);
  const [planModule, setPlanModule] = useState<boolean>(user.features.planModule);
  const [vocabularyModule, setVocabularyModule] = useState<boolean>(user.features.vocabularyModule);

  const handleSubmit = () => {
    setOpenLoading(true);
    UserService.updateUser({
      id: user.id,
      email: email,
      username: user.username,
      firstName: firstName,
      lastName: lastName,
      phoneNum: phoneNum,
      address: address,
      avatarUrl: avatarUrl,
      introduction: introduction,
      blog: blog,
      profession: profession,
      backgroundImg: backgroundImg,
      photoImg: photoImg,
      features: {
        articleModule: articleModule,
        planModule: planModule,
        vocabularyModule: vocabularyModule
      },
    })
      .then(response => {
        dispatch({ type: 'LOGIN_SUCCESS', payload: response.data });
        localStorage.setItem(ELocalStorageKeys.CURRENT_USER, JSON.stringify(response.data));
        enqueueSnackbar(t('profile-setting.msg.success'), {
          variant: 'success',
        });
      })
      .catch(() => {
        enqueueSnackbar(t('profile-setting.msg.error'), {
          variant: 'error',
        });
      })
      .finally(() => setOpenLoading(false));
  };

  return (
    <Page
      openLoading={openLoading}
      pageTitle={t('profile-setting.form-title')}
    >
      <Box>
        <InfoSection mt={8}>
          <Grid
            item
            xs={12}
            sx={{ mt: -7 }}
          >
            <AvatarSelector
              showAvatars={showAvatars}
              setShowAvatars={setShowAvatars}
              avatarUrl={avatarUrl}
              setAvatarUrl={setAvatarUrl}
            />
          </Grid>
          <QuarterRow>
            <TextField
              required
              label={t('profile-setting.fields.first-name')}
              fullWidth
              onChange={e => setFirstName(e.target.value)}
              defaultValue={firstName}
            />
          </QuarterRow>
          <QuarterRow>
            <TextField
              required
              label={t('profile-setting.fields.last-name')}
              fullWidth
              onChange={e => setLastName(e.target.value)}
              defaultValue={lastName}
            />
          </QuarterRow>
          <HalfRow>
            <TextField
              label={t('profile-setting.fields.profession')}
              fullWidth
              onChange={e => setProfession(e.target.value)}
              defaultValue={user.profession}
            />
          </HalfRow>
          <HalfRow>
            <TextField
              disabled
              InputLabelProps={{ shrink: true }}
              label={t('profile-setting.fields.email')}
              fullWidth
              type="email"
              autoComplete="email"
              onChange={e => setEmail(e.target.value)}
              defaultValue={email}
            />
          </HalfRow>
          <HalfRow>
            <TextField
              label={t('profile-setting.fields.phone-number')}
              fullWidth
              onChange={e => setPhoneNum(e.target.value)}
              defaultValue={phoneNum}
            />
          </HalfRow>
          <HalfRow>
            <TextField
              label={t('profile-setting.fields.address')}
              fullWidth
              onChange={e => setAddress(e.target.value)}
              defaultValue={address}
            />
          </HalfRow>
          <HalfRow>
            <TextField
              label={t('profile-setting.fields.blog')}
              fullWidth
              onChange={e => setBlog(e.target.value)}
              defaultValue={blog}
              placeholder={'https://xxx.blog.com'}
            />
          </HalfRow>
          <OneRow>
            <TextField
              label={t('profile-setting.fields.introduction')}
              fullWidth
              onChange={e => setIntroduction(e.target.value)}
              defaultValue={introduction}
              multiline
              minRows={3}
              placeholder={t('profile-setting.fields.introduction-placeholder')}
            />
          </OneRow>
        </InfoSection>
        <Divider/>
        <InfoSection>
          <QuarterRow>
            <FormControlLabel control={<Switch color="secondary"
                                               checked={articleModule}
                                               onChange={e => setArticleModule(e.target.checked)}/>}
                              label={t('breadcrumbs.articles')}
            />
          </QuarterRow>
          <QuarterRow>
            <FormGroup>
              <FormControlLabel control={<Switch color="secondary"
                                                 checked={planModule}
                                                 onChange={e => setPlanModule(e.target.checked)}/>}

                                label={t('breadcrumbs.plans')}
              />
            </FormGroup>
          </QuarterRow>
          <QuarterRow>
            <FormGroup>
              <FormControlLabel control={<Switch color="secondary"
                                                 checked={vocabularyModule}
                                                 onChange={e => setVocabularyModule(e.target.checked)}/>}
                                label={t('breadcrumbs.vocabularies')}
              />
            </FormGroup>
          </QuarterRow>
        </InfoSection>
        <Divider />
        <InfoSection>
          <HalfRow>
            <TextField
              label={t('profile-setting.fields.background-img')}
              fullWidth
              onChange={e => setBackgroundImg(e.target.value)}
              defaultValue={backgroundImg}
            />
          </HalfRow>
          <HalfRow>
            <TextField
              label={t('profile-setting.fields.photo-img')}
              fullWidth
              onChange={e => setPhotoImg(e.target.value)}
              defaultValue={photoImg}
            />
          </HalfRow>
        </InfoSection>
        <SaveButtonPanel primaryEvent={handleSubmit} />
      </Box>
    </Page>
  );
}

const InfoSection = (props: { children: ReactNode[]; mt?: number }) => {
  return (
    <Paper sx={{ mt: props.mt || 2, mb: 2, p: { xs: 2, md: 3 }, borderRadius: 4 }}>
      <Grid
        container
        spacing={3}
      >
        {props.children}
      </Grid>
    </Paper>
  );
};
