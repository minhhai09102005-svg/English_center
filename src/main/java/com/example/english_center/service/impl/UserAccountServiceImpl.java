package com.example.english_center.service.impl;

import com.example.english_center.builder.UserAccountSearchBuilder;
import com.example.english_center.converter.SearchBuilderConverter;
import com.example.english_center.customExceptions.ConflictException;
import com.example.english_center.customExceptions.DataNotFoundException;
import com.example.english_center.dto.request.UserAccountRequest;
import com.example.english_center.dto.request.UserAccountUpdateRequest;
import com.example.english_center.dto.response.UserAccountResponse;
import com.example.english_center.entity.StudentProfiles;
import com.example.english_center.entity.TeacherProfiles;
import com.example.english_center.entity.Users;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;
import com.example.english_center.repository.StudentProfilesRepository;
import com.example.english_center.repository.TeacherProfilesRepository;
import com.example.english_center.repository.UsersRepository;
import com.example.english_center.service.UserAccountService;
import com.example.english_center.specification.UserAccountSpecs;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final SearchBuilderConverter searchBuilderConverter;
    private final ModelMapper modelMapper;
    private final TeacherProfilesRepository teacherProfilesRepository;
    private final StudentProfilesRepository studentProfilesRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String createTeacherAccount(UserAccountRequest request) {
        Users user = createUserAccount(request, Role.TEACHER);

        TeacherProfiles profile = new TeacherProfiles();
        profile.setUser(user);
        profile.generateTeacherCode();
        teacherProfilesRepository.save(profile);

        return "Tạo tài khoản giáo viên thành công";
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String createStudentAccount(UserAccountRequest request) {
        Users user = createUserAccount(request, Role.STUDENT);

        StudentProfiles profile = new StudentProfiles();
        profile.setUser(user);
        profile.generateStudentCode();
        studentProfilesRepository.save(profile);

        return "Tạo tài khoản học sinh thành công";
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public List<UserAccountResponse> searchUserAccounts(Map<String, Object> params) {
        UserAccountSearchBuilder userAccountSearchBuilder = searchBuilderConverter.toUserAccountSearchBuilder(params);
        var userAccountSpecs = UserAccountSpecs.byBuilder(userAccountSearchBuilder);
        List<Users> users = usersRepository.findAll(userAccountSpecs);
        if (users.isEmpty()) {
            throw DataNotFoundException.userAccount();
        }

        List<UserAccountResponse> userAccounts = new ArrayList<>();
        for (Users user : users) {
            userAccounts.add(modelMapper.map(user, UserAccountResponse.class));
        }
        return userAccounts;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public UserAccountResponse getUserAccountInfo(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(DataNotFoundException::userAccount);
        return modelMapper.map(user, UserAccountResponse.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public UserAccountResponse updateUserAccount(Long userId, UserAccountUpdateRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(DataNotFoundException::userAccount);
        String email = request.getEmail().trim().toLowerCase();
        String phoneNumber = request.getPhoneNumber().trim();

        usersRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    throw ConflictException.duplicateEmail();
                });

        usersRepository.findByPhoneNumber(phoneNumber)
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    throw ConflictException.duplicatePhoneNumber();
                });

        String avatarUrl = request.getAvatarUrl();

        user.setFullname(request.getFullname().trim());
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setGender(request.getGender());
        user.setAvatarUrl(avatarUrl == null || avatarUrl.isBlank() ? null : avatarUrl.trim());

        Users updatedUser = usersRepository.save(user);
        return modelMapper.map(updatedUser, UserAccountResponse.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String deactivateUserAccount(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(DataNotFoundException::userAccount);
        user.setStatus(UserStatus.INACTIVE);
        usersRepository.save(user);
        return "Khóa tài khoản thành công";
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String activateUserAccount(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(DataNotFoundException::userAccount);
        user.setStatus(UserStatus.ACTIVE);
        usersRepository.save(user);
        return "Mở khóa tài khoản thành công";
    }

    private Users createUserAccount(UserAccountRequest request, Role role) {
        String email = request.getEmail().trim().toLowerCase();
        String phoneNumber = request.getPhoneNumber().trim();

        if (usersRepository.existsByEmail(email)) {
            throw ConflictException.duplicateEmail();
        }

        if (usersRepository.existsByPhoneNumber(phoneNumber)) {
            throw ConflictException.duplicatePhoneNumber();
        }

        Users user = new Users();
        user.setFullname(request.getFullname().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(phoneNumber);
        user.setGender(request.getGender());
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        return usersRepository.save(user);
    }
}
