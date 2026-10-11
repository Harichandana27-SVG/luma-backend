package com.luma.init;

import com.luma.model.*;
import com.luma.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private VaultEntryRepository vaultEntryRepository;

    @Autowired
    private OpenWhenCapsuleRepository capsuleRepository;

    @Autowired
    private VaultAuditLogRepository auditLogRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        System.out.println("🌱 Initializing LUMA demo seed data...");

        // 1. Create Users
        String encodedPass = passwordEncoder.encode("password123");
        String encodedPin = passwordEncoder.encode("1234");

        User hari = new User("hari", "hari@luma.social", encodedPass, "Hari Chandhana");
        hari.setBio("Software craftsman, twilight thinker, & curator of meaningful digital memories.");
        hari.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80");
        hari.setVaultPin(encodedPin);
        hari = userRepository.save(hari);

        User sarah = new User("sarah_art", "sarah@luma.social", encodedPass, "Sarah Jenkins");
        sarah.setBio("Visual designer, analog film enthusiast, and quiet tea drinker.");
        sarah.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80");
        sarah.setVaultPin(encodedPin);
        sarah = userRepository.save(sarah);

        User alex = new User("alex_dev", "alex@luma.social", encodedPass, "Alex Rivera");
        alex.setBio("Building open web experiments & cycling through misty coastal highways.");
        alex.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80");
        alex.setVaultPin(encodedPin);
        alex = userRepository.save(alex);

        User maya = new User("maya_travels", "maya@luma.social", encodedPass, "Maya Lin");
        maya.setBio("Documenting hidden alleyways, mountain trails, and street coffee.");
        maya.setAvatarUrl("https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80");
        maya.setVaultPin(encodedPin);
        maya = userRepository.save(maya);

        // 2. Follow Relationships
        followRepository.save(new Follow(hari, sarah));
        followRepository.save(new Follow(hari, alex));
        followRepository.save(new Follow(hari, maya));
        followRepository.save(new Follow(sarah, hari));
        followRepository.save(new Follow(alex, hari));
        followRepository.save(new Follow(maya, hari));

        // 3. Social Posts
        Post p1 = new Post(
                sarah,
                "The golden hour light through our studio windows this afternoon. Sometimes taking a step back makes everything fall into harmony. ✨ #VisualArts #GoldenHour",
                "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800&auto=format&fit=crop&q=80",
                "#VisualArts"
        );
        p1 = postRepository.save(p1);

        Post p2 = new Post(
                alex,
                "Finally completed our distributed cache benchmark after 3 sleepless nights. Grateful for clean code and strong espresso. ☕💻 #DevLife #Engineering",
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&auto=format&fit=crop&q=80",
                "#Engineering"
        );
        p2 = postRepository.save(p2);

        Post p3 = new Post(
                maya,
                "Dawn reflections over the misty fjord in Norway. Not a single sound except the gentle ripples of cold water. 🏔️🌲 #Wanderlust #Nature",
                "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
                "#Wanderlust"
        );
        p3 = postRepository.save(p3);

        Post p4 = new Post(
                hari,
                "Welcome to LUMA. Built around the philosophy that not all digital expression belongs on a public billboard — some of our deepest moments deserve sacred private keeping. 🌌✨ #LUMA #PrivacyFirst",
                "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=800&auto=format&fit=crop&q=80",
                "#LUMA"
        );
        p4 = postRepository.save(p4);

        // 4. Comments & Likes
        commentRepository.save(new Comment(p1, hari, "The lighting is breathtaking Sarah! Reminds me of the old gallery downtown."));
        commentRepository.save(new Comment(p1, alex, "Stunning composition!"));
        commentRepository.save(new Comment(p4, sarah, "So proud of this vision. The Vault concept is game changing."));

        postLikeRepository.save(new PostLike(p1, hari));
        postLikeRepository.save(new PostLike(p1, alex));
        postLikeRepository.save(new PostLike(p2, hari));
        postLikeRepository.save(new PostLike(p3, hari));
        postLikeRepository.save(new PostLike(p4, sarah));
        postLikeRepository.save(new PostLike(p4, alex));

        // 5. LUMA Vault Entries (Private space)
        VaultEntry v1 = new VaultEntry(
                hari,
                "Quiet Reflections on Growth & Direction",
                "Today I realized that peace doesn't come from finishing everything on my checklist; it comes from deciding what doesn't belong on it in the first place. Keeping this here as a compass whenever life speeds up too fast.",
                VaultEntryType.FEELING,
                VaultVisibility.PRIVATE
        );
        v1.setMoodEmoji("🌿");
        v1.setEncrypted(true);
        vaultEntryRepository.save(v1);

        VaultEntry v2 = new VaultEntry(
                hari,
                "The Night We Decided to Build LUMA",
                "Late night brainstorm session over warm green tea. We mapped out how social networks forgot intimacy and privacy. This memory captures the exact moment the vision became crystal clear.",
                VaultEntryType.MEMORY,
                VaultVisibility.TRUSTED_CIRCLE
        );
        v2.setMoodEmoji("💡");
        v2.setTrustedUsernames("sarah_art,alex_dev");
        v2.setMediaUrl("https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=800&auto=format&fit=crop&q=80");
        v2.setEncrypted(true);
        vaultEntryRepository.save(v2);

        VaultEntry v3 = new VaultEntry(
                hari,
                "Design Draft: Next-Gen Encryption Key Ceremony",
                "Temporary confidential notes for the architecture review next week. Revokes automatically after the sprint concludes.",
                VaultEntryType.NOTE,
                VaultVisibility.TEMPORARY_ACCESS
        );
        v3.setMoodEmoji("🔒");
        v3.setTrustedUsernames("alex_dev");
        v3.setExpiresAt(LocalDateTime.now().plusDays(7));
        v3.setEncrypted(true);
        vaultEntryRepository.save(v3);

        VaultEntry v4 = new VaultEntry(
                sarah,
                "Secret Sketchbook Collection - 35mm Scans",
                "Personal uncensored sketches and private photographs. Sharing with Hari for creative input.",
                VaultEntryType.PHOTOGRAPH,
                VaultVisibility.TRUSTED_CIRCLE
        );
        v4.setMoodEmoji("🎨");
        v4.setTrustedUsernames("hari");
        v4.setMediaUrl("https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=800&auto=format&fit=crop&q=80");
        vaultEntryRepository.save(v4);

        // 6. Open When Capsules ("Open When" Distinctive Feature)
        // Capsule 1: Ready to unlock now! (Unlock date passed)
        OpenWhenCapsule c1 = new OpenWhenCapsule(
                hari,
                "hari",
                "Open When You Need A Reminder of Why You Started",
                "FUTURE_SELF",
                "Hey Hari. If you're reading this, you might be facing friction or self-doubt. Remember the excitement you felt on day one. You don't need everyone's approval to create something deeply honest and meaningful. Take a deep breath and keep building.",
                LocalDateTime.now().minusHours(2), // Already ready to unlock!
                "A letter penned to your future self on a quiet evening."
        );
        c1.setLockedMediaUrl("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80");
        capsuleRepository.save(c1);

        // Capsule 2: Locked for the future (Birthday / Graduation Milestone)
        OpenWhenCapsule c2 = new OpenWhenCapsule(
                sarah,
                "hari",
                "Open When You Launch LUMA 1.0 to the World",
                "MILESTONE",
                "CONGRATULATIONS HARI! 🎉 You poured your soul into creating a space that respects human dignity and digital boundaries. We made it happen! Now celebrate!",
                LocalDateTime.now().plusDays(5).plusHours(14), // Locked in the future!
                "A surprise congratulatory message locked until the big launch day."
        );
        c2.setUnlockPasscode("LAUNCH2026");
        capsuleRepository.save(c2);

        // Capsule 3: Time capsule locked until New Year
        OpenWhenCapsule c3 = new OpenWhenCapsule(
                hari,
                "hari",
                "Open On New Year's Eve at Midnight",
                "ANNIVERSARY",
                "Looking back at the past 365 days. Every struggle was a stepping stone. Here's to courage, compassion, and boundless curiosity in the coming year.",
                LocalDateTime.now().plusMonths(2),
                "Reflections sealed for December 31st."
        );
        capsuleRepository.save(c3);

        // 7. Vault Audit Logs (Authentic security trail)
        auditLogRepository.save(new VaultAuditLog(hari, "VAULT_UNLOCKED", "Vault unlocked via master PIN 1234", "127.0.0.1"));
        auditLogRepository.save(new VaultAuditLog(hari, "ENTRY_CREATED", "Created vault item: \"Quiet Reflections on Growth & Direction\" (PRIVATE)", "127.0.0.1"));
        auditLogRepository.save(new VaultAuditLog(hari, "ENTRY_CREATED", "Created vault item: \"The Night We Decided to Build LUMA\" (TRUSTED_CIRCLE)", "127.0.0.1"));
        auditLogRepository.save(new VaultAuditLog(hari, "CAPSULE_CREATED", "Created Open When capsule: \"Open When You Need A Reminder of Why You Started\"", "127.0.0.1"));

        // 8. Notifications
        notificationRepository.save(new Notification(hari, sarah, "VAULT_SHARED", "@sarah_art shared a private Vault memory with you: \"Secret Sketchbook Collection\"", v4.getId()));
        notificationRepository.save(new Notification(hari, sarah, "CAPSULE_RECEIVED", "@sarah_art sent you a locked Open When capsule: \"Open When You Launch LUMA 1.0\"", c2.getId()));
        notificationRepository.save(new Notification(hari, sarah, "LIKE", "@sarah_art liked your post.", p4.getId()));

        System.out.println("✅ LUMA seed data initialized successfully!");
        System.out.println("   👤 Default accounts: 'hari', 'sarah_art', 'alex_dev', 'maya_travels'");
        System.out.println("   🔑 Password: 'password123' | Vault PIN: '1234'");
    }
}
